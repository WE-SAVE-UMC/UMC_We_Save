package com.example.we_save.ui.accident

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.we_save.data.model.Accident
import com.example.we_save.data.model.AccidentWithComments
import com.example.we_save.domain.repositories.AccidentRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AccidentDetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AccidentRepositoryImpl.getInstance(application)
    private var accident: Accident? = null

    val imagePickerSheetVisibility = MutableStateFlow(false)

    private val _replyImages = MutableStateFlow<List<String>>(listOf())
    val replyImage: StateFlow<List<String>> = _replyImages

    val replyMessage = MutableStateFlow<String>("")

    fun getAccidentWithComments(accident: Accident): Flow<AccidentWithComments?> {
        this.accident = accident
        return repository.getAccident(accident.id)
    }

    fun addReplyImages(urls: List<String>) {
        _replyImages.value = ArrayList(_replyImages.value + urls).take(5)
    }

    fun setReplyImages(urls: List<String>) {
        _replyImages.value = ArrayList(urls).take(5)
    }

    suspend fun send() {
        accident?.let {
            repository.createComment(it.id, replyMessage.value, _replyImages.value)

            _replyImages.value = listOf()
            replyMessage.value = ""
        }
    }

    suspend fun remove() {
        accident?.let {
            repository.removeAccident(it)
        }
    }

    suspend fun endSituation() {
        accident?.let {
            repository.updateAccident(it.copy(isEndSituation = true))
        }
    }
}