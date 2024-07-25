package com.example.we_save.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Accident::class,
            parentColumns = ["id"],
            childColumns = ["accident_id"],
            onDelete = CASCADE
        )
    ]
)
data class Comment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val message: String = "",
    val images: List<String> = listOf(),
    val timestamp: Long = Date().time,
    @ColumnInfo("accident_id") val accidentId: Long = 0,
)
