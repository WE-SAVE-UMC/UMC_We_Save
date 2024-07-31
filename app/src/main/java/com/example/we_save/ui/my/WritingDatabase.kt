package com.example.we_save.ui.my

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Writing::class, Block::class], version = 1)
abstract class WritingDatabase: RoomDatabase(){

    abstract fun writingDao(): WritingDao

    abstract fun blockDao(): BlockDao

    companion object {
        private var instance: WritingDatabase? = null

        @Synchronized
        fun getInstance(context: Context): WritingDatabase? {
            if(instance == null) {
                synchronized(WritingDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WritingDatabase::class.java,
                        "writing-database" // 다른 데이터 베이스랑 이름 겹치면 꼬임
                    ).allowMainThreadQueries().build()
                }
            }

            return instance
        }
    }

}
