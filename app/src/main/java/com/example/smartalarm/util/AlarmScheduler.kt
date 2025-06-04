package com.example.smartalarm.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.getSystemService
import com.example.smartalarm.data.repository.AlarmRepository
import com.example.smartalarm.domain.model.Alarm
import com.example.smartalarm.receiver.AlarmReceiver
import com.example.smartalarm.util.Constants.ACTION_DISMISS
import com.example.smartalarm.util.Constants.EXTRA_ALARM_ID
import com.example.smartalarm.util.Constants.EXTRA_ALARM_LABEL
import com.example.smartalarm.util.Constants.EXTRA_ALARM_TIME
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmRepository: AlarmRepository
) {
    private val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val scope = CoroutineScope(Dispatchers.IO)

    fun schedule(alarm: Alarm) {
        if (!alarm.isEnabled) {
            cancel(alarm)
            return
        }

        val triggerTime = getNextTriggerTime(alarm)
        
        val intent = createAlarmIntent(alarm, triggerTime)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmClock = AlarmManager.AlarmClockInfo(
            triggerTime,
            getAlarmInfoPendingIntent(alarm)
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    fun cancel(alarm: Alarm) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_DISMISS
            putExtra(EXTRA_ALARM_ID, alarm.id)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
    }

    suspend fun rescheduleAllAlarms() {
        val alarms = alarmRepository.getAlarms().first()
        alarms.forEach { alarm ->
            if (alarm.isEnabled) {
                schedule(alarm)
            }
        }
    }

    private fun createAlarmIntent(alarm: Alarm, triggerTime: Long): Intent {
        return Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_ALARM_ID, alarm.id)
            putExtra(EXTRA_ALARM_TIME, triggerTime)
            putExtra(EXTRA_ALARM_LABEL, alarm.label)
        }
    }

    private fun getNextTriggerTime(alarm: Alarm): Long {
        val now = LocalDateTime.now()
        var nextTrigger = LocalDateTime.of(now.toLocalDate(), alarm.time)
        
        // If the time has already passed today, schedule for the next occurrence
        if (nextTrigger.isBefore(now)) {
            nextTrigger = nextTrigger.plusDays(1)
        }
        
        // If it's a recurring alarm, find the next occurrence
        if (alarm.days.isNotEmpty()) {
            nextTrigger = findNextRecurrence(alarm.time, alarm.days.map { it.value }.toSet())
        }
        
        return nextTrigger.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
    
    private fun findNextRecurrence(time: LocalTime, days: Set<Int>): LocalDateTime {
        val now = LocalDateTime.now()
        var next = LocalDateTime.of(now.toLocalDate(), time)
        
        // If the time has already passed today, check the next day
        if (next.isBefore(now)) {
            next = next.plusDays(1)
        }
        
        // Find the next day of the week that matches one of the selected days
        while (!days.contains(next.dayOfWeek.value)) {
            next = next.plusDays(1)
        }
        
        return next
    }

    private fun getAlarmInfoPendingIntent(alarm: Alarm): PendingIntent {
        val showIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_ALARM_ID, alarm.id)
        }

        return PendingIntent.getActivity(
            context,
            alarm.id.toInt() * 10, // Different request code to avoid collision
            showIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
