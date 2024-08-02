package com.example.we_save.data.sources.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.we_save.data.model.Accident
import com.example.we_save.data.model.AccidentWithComments
import kotlinx.coroutines.flow.Flow

@Dao
interface AccidentDao {
    @Insert
    suspend fun insertAccident(accident: Accident)

    @Update
    suspend fun updateAccident(accident: Accident)

    @Delete
    suspend fun removeAccident(accident: Accident)

    @Query("SELECT * FROM accident WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY id DESC")
    fun getAccidents(query: String): Flow<List<Accident>>

    @Query("SELECT * FROM accident ORDER BY id DESC LIMIT 5")
    fun getRecentAccidents(): Flow<List<Accident>>

    @Query("SELECT * FROM accident WHERE id = :id")
    fun getAccident(id: Long): Flow<AccidentWithComments?>
}