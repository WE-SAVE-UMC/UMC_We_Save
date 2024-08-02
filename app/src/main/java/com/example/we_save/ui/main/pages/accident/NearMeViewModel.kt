package com.example.we_save.ui.main.pages.accident

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.we_save.domain.repositories.AccidentRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest

class NearMeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AccidentRepositoryImpl.getInstance(application)

    val query = MutableStateFlow<String>("")
    val excludeSituationEnd = MutableStateFlow(false)
    val filter = MutableStateFlow(Filter.DISTANCE)

    val recentAccidents = repository.getRecentAccidents()

    val accidents = combine(listOf(query.debounce(300), excludeSituationEnd)) {
        it
    }.flatMapLatest {
        val query = it[0] as String
        val excludeSituationEnd = it[1] as Boolean

        repository.getAccidents(query).mapLatest {
            if (excludeSituationEnd) {
                it.filter { !it.isEndSituation }
            } else {
                it
            }
        }
    }

    enum class Filter {
        DISTANCE, RECENT, CHECK
    }
}