package com.example.we_save.ui.main.pages.accident

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.we_save.common.Constants
import com.example.we_save.data.model.ErrorResponse
import com.example.we_save.data.model.PostDto
import com.example.we_save.data.model.ReverseGeocoding
import com.example.we_save.domain.repositories.PostsRepositoryImpl
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NearMeViewModel(application: Application) : AndroidViewModel(application) {
    enum class Filter(val param: String) {
        DISTANCE("distance"), RECENT("recent"), TOP("top")
    }

    private val repository = PostsRepositoryImpl.getInstance(application)
    private var page: Int = 0
        set(value) {
            field = value
            if (page == 0) isEnd = false
        }

    private var isEnd = false
    private var isLoading = false

    val ticker by lazy {
        flow {
            while (true) {
                delay(5000)
                emit(1)
            }
        }.shareIn(viewModelScope, started = SharingStarted.Lazily)
    }

    val address = MutableStateFlow<ReverseGeocoding?>(null)
    val query = MutableStateFlow("")
    val excludeSituationEnd = MutableStateFlow(false)
    val filter = MutableStateFlow(Filter.DISTANCE)

    val recentPosts = MutableStateFlow<List<PostDto>?>(null)
    val posts = MutableStateFlow<List<PostDto>?>(null)

    init {
        viewModelScope.launch {
            address.collectLatest {
                refresh()
            }
        }

        viewModelScope.launch {
            combine(listOf(query, excludeSituationEnd, filter)) { it }.collectLatest {
                page = 0
                loadMore()
            }
        }
    }

    suspend fun refresh() = withContext(Dispatchers.IO) {
        val address = this@NearMeViewModel.address.value ?: return@withContext

        listOf(
            async {
                try {
                    val response = repository.getNearByPosts(
                        Filter.TOP.param,
                        0,
                        true,
                        address.postsRequest
                    ).body() ?: return@async
                    recentPosts.tryEmit(response.result.postDTOs)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            async {
                page = 0
                loadMore()
            }
        ).awaitAll()
    }

    suspend fun loadMore() = withContext(Dispatchers.IO) {
        if (isEnd) return@withContext
        if (isLoading) return@withContext

        val address = this@NearMeViewModel.address.value ?: return@withContext
        val page = this@NearMeViewModel.page

        try {
            isLoading = true

            val result = if (Constants.POST_GET_METHOD == 0) {
                repository.getNearByPosts(
                    filter.value.param,
                    page,
                    excludeSituationEnd.value,
                    address.postsRequest
                )
            } else {
                repository.searchNearByPosts(
                    query.value,
                    filter.value.param,
                    page,
                    excludeSituationEnd.value,
                    address.postsRequest
                )
            }

            if (result.errorBody() != null) {
                try {
                    val errorResponse = Gson().fromJson<ErrorResponse>(
                        result.errorBody()!!.string(),
                        ErrorResponse::class.java
                    )
                    Log.d(
                        "NearViewModel", """
                        ${errorResponse.message}
                        ${errorResponse.result}
                    """.trimIndent()
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val response = result.body()
            if (response == null) {
                isLoading = false
                return@withContext
            }

            this@NearMeViewModel.page++
            isLoading = false

            if (response.result.postDTOs.size < 10) {
                isEnd = true
            }

            if (page > 0) {
                posts.tryEmit((posts.value ?: listOf()) + response.result.postDTOs)
            } else {
                posts.tryEmit(listOf())
                delay(50)
                posts.tryEmit(response.result.postDTOs)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}