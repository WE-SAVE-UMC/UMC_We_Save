package com.example.we_save.data.sources.room

import androidx.room.TypeConverter
import com.example.we_save.domain.model.AccidentType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AppTypeConverters {
    @TypeConverter
    fun intToAccidentType(value: Int) = AccidentType.entries[value]

    @TypeConverter
    fun accidentTypeToInt(value: AccidentType) = value.ordinal

    @TypeConverter
    fun stringToStringList(value: String) = Json.decodeFromString<List<String>>(value)

    @TypeConverter
    fun stringListToString(value: List<String>) = Json.encodeToString(value)
}