package com.example.we_save.data.sources.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.we_save.data.model.Accident
import com.example.we_save.data.model.Comment

@Database(entities = [Accident::class, Comment::class], version = 2)
@TypeConverters(AppTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accidentDao(): AccidentDao

    abstract fun commentDao(): CommentDao
}