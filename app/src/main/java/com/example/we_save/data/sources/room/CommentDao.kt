package com.example.we_save.data.sources.room

import androidx.room.Dao
import androidx.room.Insert
import com.example.we_save.data.model.Comment

@Dao
interface CommentDao {
    @Insert
    suspend fun insertComment(comment: Comment)
}