package com.example.smartalarm.data.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.AlarmManagerCompat
import com.example.smartalarm.R
import com.example.smartalarm.domain.model.Alarm
import com.example.smartalarm.domain.repository.AlarmScheduler
import com.example.smartalarm.receiver.AlarmReceiver
import com.example.smartalarm.util.Constants
import com.example.smartalarm.util.extensions.getAlarmManager
import com.example.smartalarm.util.extensions.getNextAlarmTime
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [AlarmScheduler] that uses Android's [AlarmManager] to schedule alarms.
 */
@Singleton
class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AlarmScheduler {

    private val alarmManager: AlarmManager = context.getAlarmManager()

    override suspend fun scheduleAlarm(
        alarm: Alarm,
        triggerTime: LocalDateTime,
        isExact: Boolean
    ) = withContext(Dispatchers.IO) {
        val triggerAtMillis = triggerTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val pendingIntent = createAlarmPendingIntent(alarm.id.toInt(), alarm)

        if (isExact && canScheduleExactAlarms()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                @Suppress("DEPRECATION")
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } else {
            AlarmManagerCompat.setAndAllowWhileIdle(
                alarmManager,
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    override suspend fun cancelAlarm(alarm: Alarm) = withContext(Dispatchers.IO) {
        val pendingIntent = createAlarmPendingIntent(
            alarm.id.toInt(), 
            alarm, 
            PendingIntent.FLAG_NO_CREATE
        )
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }

    override suspend fun rescheduleAllAlarms() {
        // This will be implemented to reschedule all active alarms
        // after device reboot or app update
    }

    override suspend fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    override suspend fun requestExactAlarmPermission(): Boolean {
        // This should launch the system settings screen for the user to grant the permission
        // Return true if permission is already granted or was just granted
        return canScheduleExactAlarms()
    }

    override suspend fun scheduleRepeatingAlarm(
        alarm: Alarm,
        daysOfWeek: Set<Int>,
        time: java.time.LocalTime
    ) = withContext(Dispatchers.IO) {
        val nextTriggerTime = getNextAlarmTime(time, daysOfWeek)
        scheduleAlarm(alarm, nextTriggerTime)
    }

    override suspend fun scheduleOneTimeAlarm(
        alarm: Alarm,
        triggerTime: java.time.LocalDateTime
    ) = withContext(Dispatchers.IO) {
        scheduleAlarm(alarm, triggerTime)
    }

    override suspend fun scheduleSmartAlarm(
        alarm: Alarm,
        destinationTime: LocalDateTime,
        travelTimeMinutes: Int
    ) = withContext(Dispatchers.IO) {
        val triggerTime = destinationTime.minusMinutes(travelTimeMinutes.toLong())
        scheduleAlarm(alarm, triggerTime)
    }

    override suspend fun scheduleNextRepeatingAlarm(
        alarm: Alarm,
        currentTime: LocalDateTime
    ) = withContext(Dispatchers.IO) {
        if (alarm.days.isNotEmpty()) {
            val nextTriggerTime = getNextAlarmTime(
                alarm.time,
                alarm.days.map { it.value }.toSet(),
                currentTime
            )
            scheduleAlarm(alarm, nextTriggerTime)
        }
    }

    override suspend fun cancelAllAlarms() {
        // This will be implemented to cancel all scheduled alarms
        // Typically used when user logs out or disables all alarms
    }


    override suspend fun isAlarmScheduled(alarmId: Long): Boolean {
        return PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            createAlarmIntent(alarmId),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        ) != null
    }

    private fun createAlarmPendingIntent(
        requestCode: Int,
        alarm: Alarm,
        flags: Int = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    ): PendingIntent? {
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            createAlarmIntent(alarm.id),
            flags
        )
    }

    private fun createAlarmIntent(alarmId: Long): Intent {
        return Intent(context, AlarmReceiver::class.java).apply {
            action = Constants.ACTION_ALARM_TRIGGER
            putExtra(Constants.EXTRA_ALARM_ID, alarmId)
        }
    }

    companion object {
        private const val TAG = "AlarmSchedulerImpl"
    }
}
