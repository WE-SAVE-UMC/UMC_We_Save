package com.example.we_save.ui.accident

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import com.example.we_save.data.model.Accident
import com.example.we_save.domain.model.AccidentType
import com.example.we_save.domain.repositories.AccidentRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class AccidentEditorViewModel(application: Application) : AndroidViewModel(application) {
    val repository = AccidentRepositoryImpl.getInstance(application)

    val title = MutableStateFlow("")
    val location = MutableStateFlow<Location?>(null)
    val address = MutableStateFlow<String?>(null)
    val type = MutableStateFlow<AccidentType?>(null)
    val description = MutableStateFlow("")
    val images = MutableStateFlow(listOf<String>())

    val isValid = combine(listOf(title, address, type, description)) {
        (it[0] as String).isNotBlank() &&
                (it[1] as? String) != null &&
                (it[2] as? AccidentType) != null &&
                (it[3] as String).isNotBlank()
    }

    fun addImages(urls: List<String>) {
        images.value = ArrayList(images.value + urls).take(10)
    }

    fun setImages(urls: List<String>) {
        images.value = ArrayList(urls).take(10)
    }

    suspend fun register() {
        val location = this.location.value!!
        val address = this.address.value!!
        val type = this.type.value!!
        val title = this.title.value
        val description = this.description.value
        val images = this.images.value

        repository.createAccident(
            location.latitude,
            location.longitude,
            address,
            title,
            description,
            type,
            images
        )
    }

    suspend fun update(accident: Accident) {
        val type = this.type.value!!
        val title = this.title.value
        val description = this.description.value
        val images = this.images.value

        repository.updateAccident(
            accident.copy(type = type, title = title, description = description, images = images)
        )
    }
}