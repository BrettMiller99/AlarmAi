package com.example.smartalarm.data.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.smartalarm.domain.model.Alarm
import com.example.smartalarm.domain.repository.AlarmScheduler
import com.example.smartalarm.receiver.AlarmReceiver
import com.example.smartalarm.util.Constants
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmSchedulerImpl @Inject constructor(
    private val context: Context,
    private val alarmRepository: com.example.smartalarm.data.repository.AlarmRepository
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(alarm: Alarm) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(Constants.EXTRA_ALARM_ID, alarm.id)
            putExtra(Constants.EXTRA_ALARM_TIME, alarm.time.toString())
            alarm.label?.let { putExtra(Constants.EXTRA_ALARM_LABEL, it) }
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = getTriggerTime(alarm)
        
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


    override fun cancel(alarm: Alarm) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    override suspend fun rescheduleAllAlarms() {
        val alarms = alarmRepository.getAlarms().first()
        alarms.forEach { alarm ->
            if (alarm.isEnabled) {
                schedule(alarm)
            }
        }
    }

    private fun getTriggerTime(alarm: Alarm): Long {
        val now = LocalDateTime.now()
        var triggerTime = LocalDateTime.of(now.toLocalDate(), alarm.time)
        
        // If the time has already passed today, schedule for the next day
        if (triggerTime.isBefore(now)) {
            triggerTime = triggerTime.plusDays(1)
        }
        
        // If it's a recurring alarm, find the next occurrence
        if (alarm.days.isNotEmpty()) {
            triggerTime = findNextRecurrence(alarm.time, alarm.days.map { it.value }.toSet())
        }
        
        return triggerTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
    
    private fun findNextRecurrence(time: java.time.LocalTime, days: Set<Int>): LocalDateTime {
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
}
