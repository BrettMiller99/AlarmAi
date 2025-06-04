package com.example.smartalarm.util

import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Utility class for date and time operations.
 */
object DateTimeUtils {
    
    // Common date and time formatters
    val TIME_FORMATTER_12H: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    val TIME_FORMATTER_24H: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy")
    val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy h:mm a")
    
    /**
     * Converts a timestamp in milliseconds to a [LocalDateTime].
     */
    fun Long.toLocalDateTime(): LocalDateTime {
        return Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()
    }
    
    /**
     * Converts a [LocalDateTime] to a timestamp in milliseconds.
     */
    fun LocalDateTime.toTimestamp(): Long {
        return atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
    
    /**
     * Converts a [LocalTime] to a formatted string.
     * @param use24HourFormat Whether to use 24-hour format (default is false)
     */
    fun LocalTime.format(use24HourFormat: Boolean = false): String {
        return if (use24HourFormat) {
            format(TIME_FORMATTER_24H)
        } else {
            format(TIME_FORMATTER_12H)
        }
    }
    
    /**
     * Parses a time string to a [LocalTime].
     * @param timeString The time string to parse (in format "h:mm a" or "HH:mm")
     */
    fun parseTime(timeString: String): LocalTime? {
        return try {
            LocalTime.parse(timeString, TIME_FORMATTER_12H)
        } catch (e: Exception) {
            try {
                LocalTime.parse(timeString, TIME_FORMATTER_24H)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    /**
     * Gets the current time as a [LocalTime].
     */
    fun currentTime(): LocalTime = LocalTime.now()
    
    /**
     * Gets the current date and time as a [LocalDateTime].
     */
    fun currentDateTime(): LocalDateTime = LocalDateTime.now()
    
    /**
     * Gets the current date as a [LocalDate].
     */
    fun currentDate(): LocalDate = LocalDate.now()
    
    /**
     * Gets the current time in milliseconds.
     */
    fun currentTimestamp(): Long = System.currentTimeMillis()
    
    /**
     * Gets the time in milliseconds until the next occurrence of the given [LocalTime].
     * If the time has already passed today, it will return the time until the same time tomorrow.
     */
    fun getMillisUntil(targetTime: LocalTime): Long {
        val now = LocalDateTime.now()
        var nextTime = now.with(targetTime)
        
        // If the time has already passed today, set it for tomorrow
        if (nextTime.isBefore(now)) {
            nextTime = nextTime.plusDays(1)
        }
        
        return Duration.between(now, nextTime).toMillis()
    }
    
    /**
     * Gets the time in milliseconds until the next occurrence of the given [LocalTime] on the specified days.
     * @param targetTime The target time
     * @param daysOfWeek The days of the week (1-7, where 1 is Sunday)
     * @return Pair of the next occurrence time and the day of week (1-7)
     */
    fun getNextOccurrence(targetTime: LocalTime, daysOfWeek: Set<Int>): Pair<LocalDateTime, Int> {
        val now = LocalDateTime.now()
        var nextDate = now.toLocalDate()
        var dayOfWeek = nextDate.dayOfWeek.value % 7 + 1 // Convert to 1-7 (Sun-Sat)
        
        // Find the next occurrence
        var daysToAdd = 0
        while (true) {
            if (daysOfWeek.contains(dayOfWeek)) {
                val candidate = nextDate.atTime(targetTime)
                if (candidate.isAfter(now)) {
                    return candidate to dayOfWeek
                }
            }
            
            // Move to next day
            nextDate = nextDate.plusDays(1)
            dayOfWeek = nextDate.dayOfWeek.value % 7 + 1
            daysToAdd++
            
            // Safety check to prevent infinite loop
            if (daysToAdd > 7) {
                // Shouldn't happen if daysOfWeek is not empty
                throw IllegalStateException("No valid day found in the next week")
            }
        }
    }
    
    /**
     * Gets the day of week name (e.g., "Monday").
     * @param dayOfWeek The day of week (1-7, where 1 is Sunday)
     * @param short Whether to return the short name (e.g., "Mon")
     */
    fun getDayName(dayOfWeek: Int, short: Boolean = false): String {
        val days = if (short) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        } else {
            listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        }
        return days.getOrElse(dayOfWeek - 1) { "" }
    }
    
    /**
     * Formats a duration in seconds to a human-readable string (e.g., "2h 30m").
     */
    fun formatDuration(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        
        return when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            else -> "${minutes}m"
        }
    }
}
