package com.example.we_save.ui.my

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "WritingTable")
data class Writing(

    var title: String? = "",
    var location: String? = "",
    var img: Int? = null,
    var finished: Boolean = false,       // 종료되면 true
    var selected: Boolean = false,  // true면 선택됨
    var selectedVisible: Boolean = false
){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}

