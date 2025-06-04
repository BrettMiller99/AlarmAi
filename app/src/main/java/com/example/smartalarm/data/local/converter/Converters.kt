package com.example.smartalarm.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.*

/**
 * Type converters for Room database to handle custom data types.
 */
class Converters {
    private val gson = Gson()

    // LocalTime converters
    @TypeConverter
    fun fromTimestamp(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(it) }
    }

    @TypeConverter
    fun localTimeToTimestamp(time: LocalTime?): String? {
        return time?.toString()
    }

    // Set<DayOfWeek> converters (stored as JSON array of integers)
    @TypeConverter
    fun fromDayOfWeekSet(value: String?): Set<DayOfWeek> {
        if (value.isNullOrBlank()) return emptySet()
        
        return try {
            val type = object : TypeToken<Set<Int>>() {}.type
            val dayValues: Set<Int> = gson.fromJson(value, type) ?: return emptySet()
            dayValues.map { DayOfWeek.of(it) }.toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    @TypeConverter
    fun dayOfWeekSetToString(days: Set<DayOfWeek>?): String {
        if (days.isNullOrEmpty()) return "[]"
        
        return try {
            val dayValues = days.map { it.value }.toSet()
            gson.toJson(dayValues)
        } catch (e: Exception) {
            "[]"
        }
    }
    
    // Set<Int> converters (for days of week as integers)
    @TypeConverter
    fun fromIntSet(value: String?): Set<Int> {
        if (value.isNullOrBlank()) return emptySet()
        
        return try {
            val type = object : TypeToken<Set<Int>>() {}.type
            gson.fromJson(value, type) ?: emptySet()
        } catch (e: Exception) {
            emptySet()
        }
    }
    
    @TypeConverter
    fun fromSet(set: Set<Int>?): String {
        if (set == null) return ""
        return gson.toJson(set)
    }
}
