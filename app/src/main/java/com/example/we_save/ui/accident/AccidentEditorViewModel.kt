package com.example.we_save.ui.accident

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.we_save.data.model.PostDetails
import com.example.we_save.data.model.PostResponse
import com.example.we_save.data.model.ReverseGeocoding
import com.example.we_save.data.sources.retrofit.PostRequest
import com.example.we_save.domain.model.AccidentType
import com.example.we_save.domain.repositories.PostsRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import retrofit2.Response

class AccidentEditorViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PostsRepositoryImpl.getInstance(application)

    val title = MutableStateFlow("")
    val address = MutableStateFlow<ReverseGeocoding?>(null)
    val type = MutableStateFlow<AccidentType?>(null)
    val description = MutableStateFlow("")
    val images = MutableStateFlow(listOf<String>())
    val report119 = MutableStateFlow(false)

    val isValid = combine(listOf(title, address, type, description)) {
        (it[0] as String).isNotBlank() &&
                (it[1] as? ReverseGeocoding) != null &&
                (it[2] as? AccidentType) != null &&
                (it[3] as String).isNotBlank()
    }

    fun addImages(urls: List<String>) {
        images.value = ArrayList(images.value + urls).take(10)
    }

    fun setImages(urls: List<String>) {
        images.value = ArrayList(urls).take(10)
    }

    private fun createPostRequest(): PostRequest {
        val address = this.address.value!!
        val type = this.type.value!!
        val title = this.title.value
        val description = this.description.value
        val report119 = this.report119.value

        return PostRequest(
            type.name,
            title,
            description,
            "PROCESSING",
            address.longitude.toString(),
            address.latitude.toString(),
            address.api,
            report119
        )
    }

    suspend fun register(): Response<PostResponse> {
        repository.temporarySave(null)
        return repository.post(createPostRequest(), images.value)
    }

    suspend fun update(details: PostDetails): Response<PostResponse> {
        return repository.edit(details.id, createPostRequest(), images.value)
    }

    fun getSavedData(): Pair<PostRequest, List<String>>? {
        return repository.temporaryPost
    }

    suspend fun temporarySave() {
        val address = this.address.value
        val type = this.type.value
        val title = this.title.value
        val description = this.description.value
        val report119 = this.report119.value

        val postRequest = PostRequest(
            type?.name,
            title,
            description,
            "PROCESSING",
            address?.longitude?.toString(),
            address?.latitude?.toString(),
            address?.api,
            report119
        )

        repository.temporarySave(postRequest, images.value)
    }
}