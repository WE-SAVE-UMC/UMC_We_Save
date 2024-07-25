package com.example.we_save.data.sources

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.room.Room
import com.example.we_save.data.model.Accident
import com.example.we_save.data.model.Comment
import com.example.we_save.data.sources.room.AppDatabase
import com.example.we_save.domain.model.AccidentType
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.Date

class AccidentLocalSource private constructor(private val context: Context) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: AccidentLocalSource? = null

        fun getInstance(context: Context): AccidentLocalSource {
            return instance ?: AccidentLocalSource(context.applicationContext).also {
                instance = it
            }
        }
    }

    private val db = Room.databaseBuilder(context, AppDatabase::class.java, "db")
        .fallbackToDestructiveMigration()
        .build()

    suspend fun createAccident(
        lat: Double,
        lng: Double,
        address: String,
        title: String,
        description: String,
        type: AccidentType,
        images: List<String>
    ) {
        val images = images.map {
            val uri = Uri.parse(it)
            val file = fileFromContentUri(context, uri)
            Uri.fromFile(file).toString()
        }

        val accident = Accident(0, lat, lng, address, title, description, type, images)
        db.accidentDao().insertAccident(accident)
    }

    fun getAccidents(query: String): Flow<List<Accident>> {
        return db.accidentDao().getAccidents(query)
    }

    fun getRecentAccidents() = db.accidentDao().getRecentAccidents()

    fun getAccident(id: Long) = db.accidentDao().getAccident(id)

    suspend fun updateAccident(accident: Accident) {
        val images = accident.images.map {
            val uri = Uri.parse(it)
            val file = fileFromContentUri(context, uri)
            Uri.fromFile(file).toString()
        }

        db.accidentDao().updateAccident(accident.copy(images = images))
    }

    suspend fun removeAccident(accident: Accident) {
        db.accidentDao().removeAccident(accident)
    }

    suspend fun createComment(accidentId: Long, message: String, images: List<String>) {
        val images = images.map {
            val uri = Uri.parse(it)
            val file = fileFromContentUri(context, uri)
            Uri.fromFile(file).toString()
        }

        val comment = Comment(0, message, images, accidentId = accidentId)
        db.commentDao().insertComment(comment)
    }

    private fun fileFromContentUri(context: Context, contentUri: Uri): File {
        val dir = File(context.filesDir, "images")
        dir.mkdirs()

        val file = File(dir, "${Date().time}.jpg")
        file.createNewFile()

        try {
            val oStream = FileOutputStream(file)
            val inputStream = context.contentResolver.openInputStream(contentUri)

            inputStream?.let {
                copy(inputStream, oStream)
            }

            oStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return file
    }

    private fun copy(source: InputStream, target: OutputStream) {
        val buf = ByteArray(8192)
        var length: Int
        while (source.read(buf).also { length = it } > 0) {
            target.write(buf, 0, length)
        }
    }
}