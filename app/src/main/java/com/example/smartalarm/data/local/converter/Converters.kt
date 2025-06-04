package com.example.smartalarm.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalTime

/**
 * Type converters for Room to convert complex types to and from primitive types.
 */
class Converters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromTimestamp(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(time: LocalTime?): String? {
        return time?.toString()
    }
    
    @TypeConverter
    fun fromIntSet(value: String?): Set<Int> {
        if (value.isNullOrEmpty()) return emptySet()
        val listType = object : TypeToken<Set<Int>>() {}.type
        return gson.fromJson(value, listType) ?: emptySet()
    }
    
    @TypeConverter
    fun fromSet(set: Set<Int>?): String {
        if (set == null) return ""
        return gson.toJson(set)
    }
}
