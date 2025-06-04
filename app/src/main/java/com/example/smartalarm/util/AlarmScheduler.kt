package com.example.smartalarm.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.AlarmManagerCompat
import com.example.smartalarm.domain.model.Alarm
import com.example.smartalarm.receiver.AlarmReceiver
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Utility class for scheduling and canceling alarms using AlarmManager.
 */
class AlarmScheduler(private val context: Context) {
    
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    /**
     * Schedules an alarm to go off at the specified time.
     * @param alarm The alarm to schedule
     * @return true if the alarm was scheduled successfully, false otherwise
     */
    fun scheduleAlarm(alarm: Alarm): Boolean {
        val alarmTime = getNextAlarmTime(alarm)
        if (alarmTime == null) {
            Timber.e("Failed to calculate next alarm time for alarm: ${alarm.id}")
            return false
        }
        
        val pendingIntent = createAlarmPendingIntent(alarm.id.toInt())
        
        try {
            // Cancel any existing alarm with the same ID
            cancelAlarm(alarm.id)
            
            // Set the alarm
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AlarmManagerCompat.setExactAndAllowWhileIdle(
                    alarmManager,
                    AlarmManager.RTC_WAKEUP,
                    alarmTime,
                    pendingIntent
                )
            } else {
                AlarmManagerCompat.setExact(
                    alarmManager,
                    AlarmManager.RTC_WAKEUP,
                    alarmTime,
                    pendingIntent
                )
            }
            
            Timber.d("Alarm scheduled: id=${alarm.id}, time=${DateTimeUtils.currentDateTime().plusMillis(alarmTime - System.currentTimeMillis())}")
            return true
        } catch (e: Exception) {
            Timber.e(e, "Failed to schedule alarm: ${alarm.id}")
            return false
        }
    }
    
    /**
     * Cancels a scheduled alarm.
     * @param alarmId The ID of the alarm to cancel
     */
    fun cancelAlarm(alarmId: Long) {
        val pendingIntent = createAlarmPendingIntent(alarmId.toInt())
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
        Timber.d("Alarm canceled: id=$alarmId")
    }
    
    /**
     * Calculates the next alarm time in milliseconds since epoch.
     * @param alarm The alarm to calculate the next trigger time for
     * @return The next alarm time in milliseconds since epoch, or null if calculation failed
     */
    private fun getNextAlarmTime(alarm: Alarm): Long? {
        return try {
            val nextOccurrence = alarm.getNextAlarmTime()
            nextOccurrence.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        } catch (e: Exception) {
            Timber.e(e, "Failed to calculate next alarm time")
            null
        }
    }
    
    /**
     * Creates a PendingIntent for the alarm.
     */
    private fun createAlarmPendingIntent(alarmId: Int): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_ALARM_TRIGGERED
            putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId)
        }
        
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        
        return PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            flags
        )
    }
    
    companion object {
        /**
         * Creates a new instance of [AlarmScheduler].
         */
        fun create(context: Context): AlarmScheduler {
            return AlarmScheduler(context.applicationContext)
        }
    }
}
