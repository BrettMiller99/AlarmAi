package com.example.smartalarm.util.extensions

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

/**
 * Extension function to get AlarmManager from Context
 */
fun Context.getAlarmManager(): AlarmManager {
    return getSystemService(Context.ALARM_SERVICE) as AlarmManager
}

/**
 * Calculate the next occurrence of an alarm time based on the given days of week.
 * @param time The time of day for the alarm
 * @param daysOfWeek Set of days (1-7 where 1 is Monday) when the alarm should repeat
 * @param from The reference date to calculate the next occurrence from (defaults to now)
 * @return The next LocalDateTime when the alarm should trigger
 */
fun getNextAlarmTime(
    time: LocalTime,
    daysOfWeek: Set<Int>,
    from: LocalDateTime = LocalDateTime.now()
): LocalDateTime {
    if (daysOfWeek.isEmpty()) {
        // If no days are selected, schedule for the next occurrence of the time today or tomorrow
        val nextTime = from.toLocalDate().atTime(time)
        return if (nextTime.isAfter(from)) nextTime else nextTime.plusDays(1)
    }

    // Convert to DayOfWeek (1-7 where 1 is Monday)
    val targetDays = daysOfWeek.map { DayOfWeek.of(it) }
    
    // Find the next occurrence
    var nextDateTime = from
    
    // If the time hasn't passed yet today and today is one of the target days
    val today = nextDateTime.toLocalDate()
    if (time.isAfter(nextDateTime.toLocalTime()) && 
        targetDays.contains(today.dayOfWeek)) {
        return today.atTime(time)
    }
    
    // Find the next day of the week that's in our target days
    var daysToAdd = 1
    while (daysToAdd <= 7) {
        val nextDate = today.plusDays(daysToAdd.toLong())
        if (targetDays.contains(nextDate.dayOfWeek)) {
            return nextDate.atTime(time)
        }
        daysToAdd++
    }
    
    // This should theoretically never happen since we're checking all 7 days
    return today.plusDays(7).with(TemporalAdjusters.nextOrSame(targetDays.first())).atTime(time)
}

/**
 * Extension function to convert LocalDateTime to milliseconds since epoch
 */
fun LocalDateTime.toEpochMillis(zoneId: ZoneId = ZoneId.systemDefault()): Long {
    return atZone(zoneId).toInstant().toEpochMilli()
}

/**
 * Extension function to check if the device is running Android 12 (API 31) or higher
 */
val isAtLeastAndroid12: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

/**
 * Extension function to check if the device is running Android 10 (API 29) or higher
 */
val isAtLeastAndroid10: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

/**
 * Extension function to check if the device is running Android 8.0 (API 26) or higher
 */
val isAtLeastAndroidO: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
