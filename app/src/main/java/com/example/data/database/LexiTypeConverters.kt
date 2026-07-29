package com.example.data.database

import androidx.room.TypeConverter
import com.example.data.Definition
import com.example.data.MasteryExercise
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LexiTypeConverters {
    @TypeConverter
    fun fromDefinitionList(value: List<Definition>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toDefinitionList(value: String): List<Definition> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromMasteryExercise(value: MasteryExercise): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toMasteryExercise(value: String): MasteryExercise {
        return Json.decodeFromString(value)
    }
}
