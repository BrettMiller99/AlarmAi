package com.example.smartalarm.domain.repository

import com.example.smartalarm.domain.model.Alarm
import java.time.LocalDateTime

/**
 * Interface for scheduling and managing alarms using the system's alarm manager.
 * This abstracts the actual scheduling implementation from the rest of the app.
 */
interface AlarmScheduler {
    /**
     * Schedule an alarm to trigger at the specified time.
     * @param alarm The alarm to schedule
     * @param triggerTime The exact time when the alarm should trigger
     * @param isExact If true, use exact alarm APIs (requires special permission)
     */
    suspend fun scheduleAlarm(alarm: Alarm, triggerTime: LocalDateTime, isExact: Boolean = true)

    /**
     * Cancel a scheduled alarm.
     * @param alarm The alarm to cancel
     */
    suspend fun cancelAlarm(alarm: Alarm)

    /**
     * Reschedule all active alarms. This is typically called after device reboot.
     */
    suspend fun rescheduleAllAlarms()

    /**
     * Check if exact alarms are allowed for this app.
     * @return true if exact alarms can be scheduled, false otherwise
     */
    suspend fun canScheduleExactAlarms(): Boolean

    /**
     * Request permission to schedule exact alarms if not already granted.
     * @return true if permission was granted or was already granted, false if denied
     */
    suspend fun requestExactAlarmPermission(): Boolean

    /**
     * Schedule a repeating alarm that triggers at the same time on specific days.
     * @param alarm The alarm to schedule
     * @param daysOfWeek Set of days when the alarm should repeat (1-7 where 1 is Monday)
     * @param time The time of day when the alarm should trigger
     */
    suspend fun scheduleRepeatingAlarm(
        alarm: Alarm,
        daysOfWeek: Set<Int>,
        time: java.time.LocalTime
    )

    /**
     * Schedule a one-time alarm that triggers at the specified date and time.
     * @param alarm The alarm to schedule
     * @param triggerTime The exact date and time when the alarm should trigger
     */
    suspend fun scheduleOneTimeAlarm(
        alarm: Alarm,
        triggerTime: java.time.LocalDateTime
    )

    /**
     * Schedule a smart alarm that accounts for travel time.
     * @param alarm The smart alarm to schedule
     * @param destinationTime The desired arrival time at the destination
     * @param travelTimeMinutes Estimated travel time in minutes
     */
    suspend fun scheduleSmartAlarm(
        alarm: Alarm,
        destinationTime: LocalDateTime,
        travelTimeMinutes: Int
    )

    /**
     * Schedule the next occurrence of a repeating alarm.
     * @param alarm The alarm to schedule
     * @param currentTime The current time to calculate the next trigger from
     */
    suspend fun scheduleNextRepeatingAlarm(alarm: Alarm, currentTime: LocalDateTime = LocalDateTime.now())

    /**
     * Cancel all scheduled alarms.
     */
    suspend fun cancelAllAlarms()

    /**
     * Check if an alarm is currently scheduled.
     * @param alarmId The ID of the alarm to check
     * @return true if the alarm is currently scheduled, false otherwise
     */
    suspend fun isAlarmScheduled(alarmId: Long): Boolean
}
