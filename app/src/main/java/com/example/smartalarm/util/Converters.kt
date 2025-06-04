package com.example.smartalarm.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalTime
import java.util.*

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.toString()
    }

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(it) }
    }

    @TypeConverter
    fun fromSetOfInts(set: Set<Int>?): String {
        return gson.toJson(set ?: emptySet<Int>())
    }

    @TypeConverter
    fun toSetOfInts(value: String?): Set<Int> {
        if (value.isNullOrEmpty()) return emptySet()
        val type = object : TypeToken<Set<Int>>() {}.type
        return gson.fromJson(value, type) ?: emptySet()
    }
}
