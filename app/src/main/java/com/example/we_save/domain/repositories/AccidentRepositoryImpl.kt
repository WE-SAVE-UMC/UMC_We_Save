package com.example.we_save.domain.repositories

import android.content.Context
import com.example.we_save.data.model.Accident
import com.example.we_save.data.sources.AccidentLocalSource
import com.example.we_save.domain.model.AccidentType
import kotlinx.coroutines.flow.Flow

class AccidentRepositoryImpl private constructor(context: Context) : AccidentRepository {
    companion object {
        private var instance: AccidentRepository? = null

        fun getInstance(context: Context): AccidentRepository {
            return instance ?: AccidentRepositoryImpl(context.applicationContext).also {
                instance = it
            }
        }
    }

    private val source = AccidentLocalSource.getInstance(context)

    override suspend fun createAccident(
        lat: Double,
        lng: Double,
        address: String,
        title: String,
        description: String,
        type: AccidentType,
        images: List<String>
    ) = source.createAccident(lat, lng, address, title, description, type, images)

    override fun getAccidents(query: String): Flow<List<Accident>> = source.getAccidents(query)

    override fun getRecentAccidents() = source.getRecentAccidents()

    override fun getAccident(id: Long) = source.getAccident(id)

    override suspend fun createComment(accidentId: Long, message: String, images: List<String>) =
        source.createComment(accidentId, message, images)

    override suspend fun updateAccident(accident: Accident) = source.updateAccident(accident)

    override suspend fun removeAccident(accident: Accident) = source.removeAccident(accident)
}