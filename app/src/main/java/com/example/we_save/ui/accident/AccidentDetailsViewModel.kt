package com.example.we_save.ui.accident

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.we_save.data.model.BaseResponse
import com.example.we_save.data.model.CommentResponse
import com.example.we_save.data.model.PostDetailsResponse
import com.example.we_save.data.sources.retrofit.CommentRequest
import com.example.we_save.domain.repositories.PostsRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class AccidentDetailsViewModel(application: Application) : AndroidViewModel(application) {
    var postId: Long = 0L
        set(value) {
            field = value
            refresh()
        }

    private val repository = PostsRepositoryImpl.getInstance(application)

    val myUserId: Long
        get() = repository.myUserId

    val isMine: Boolean
        get() = details.value?.result?.userId == myUserId

    private val _details = MutableStateFlow<PostDetailsResponse?>(null)
    val details: StateFlow<PostDetailsResponse?> = _details

    val _replyImages = MutableStateFlow<List<String>>(listOf())
    val replyImages: StateFlow<List<String>> = _replyImages

    val replyMessage = MutableStateFlow<String>("")

    fun refresh() {
        assert(postId != 0L)

        viewModelScope.launch {
            try {
                val response = repository.getDetails(postId)
                _details.tryEmit(response.body())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addReplyImages(urls: List<String>) {
        _replyImages.value = ArrayList(_replyImages.value + urls).take(5)
    }

    fun setReplyImages(urls: List<String>) {
        _replyImages.value = ArrayList(urls).take(5)
    }

    suspend fun send(commentId: Long? = null) {
        assert(postId != 0L)

        try {
            val request = CommentRequest(postId.toString(), replyMessage.value)
            val images = _replyImages.value

            if (commentId == null) {
                val response = repository.sendComment(request, images)
                // if (response.body()?.isSuccess == true) {
                // }
            } else {
                repository.editComment(commentId, request, images)
            }

            refresh()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        _replyImages.value = listOf()
        replyMessage.value = ""
    }

    suspend fun like(): Response<BaseResponse> {
        assert(postId != 0L)

        val result = repository.like(postId)
        if (result.body()?.isSuccess == true) {
            refresh()
        }

        return result
    }

    suspend fun dislike(): Response<BaseResponse> {
        assert(postId != 0L)

        val result = repository.dislike(postId)
        if (result.body()?.isSuccess == true) {
            refresh()
        }

        return result
    }

    suspend fun remove() {
        assert(postId != 0L)

        repository.remove(postId)
    }

    suspend fun complete() {
        assert(postId != 0L)

        val result = repository.complete(postId)
        if (result.body()?.isSuccess == true) {
            refresh()
        }
    }

    suspend fun report(): Response<BaseResponse> {
        assert(postId != 0L)

        return repository.report(postId)
    }

    suspend fun block(): Response<BaseResponse> {
        val details = this._details.value
        assert(details != null)

        return repository.block(details!!.result!!.userId)
    }

    suspend fun reportComment(commentId: Long) = repository.reportComment(commentId)

    suspend fun removeComment(commentId: Long): Response<CommentResponse> {
        val result = repository.removeComment(commentId)
        if (result.body()?.isSuccess == true) {
            refresh()
        }

        return result
    }
}