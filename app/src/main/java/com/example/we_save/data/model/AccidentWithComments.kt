package com.example.we_save.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class AccidentWithComments(
    @Embedded val accident: Accident,
    @Relation(
        parentColumn = "id",
        entityColumn = "accident_id"
    )
    val comments: List<Comment>
)