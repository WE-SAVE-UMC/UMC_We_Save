package com.example.we_save.ui.my

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "WritingTable")
data class Writing(

    var title: String? = "",
    var location: String? = "",
    var img: Int? = null
){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}

