package com.example.we_save.domain.repositories

import com.example.we_save.data.model.Accident
import com.example.we_save.data.model.AccidentWithComments
import com.example.we_save.domain.model.AccidentType
import kotlinx.coroutines.flow.Flow

interface AccidentRepository {
    suspend fun createAccident(
        lat: Double,
        lng: Double,
        address: String,
        title: String,
        description: String,
        type: AccidentType,
        images: List<String>
    )

    fun getAccidents(query: String): Flow<List<Accident>>

    fun getRecentAccidents(): Flow<List<Accident>>

    fun getAccident(id: Long): Flow<AccidentWithComments?>

    suspend fun createComment(accidentId: Long, message: String, images: List<String>)

    suspend fun updateAccident(accident: Accident)

    suspend fun removeAccident(accident: Accident)
}