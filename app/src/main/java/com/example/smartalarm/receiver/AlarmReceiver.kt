package com.example.smartalarm.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.smartalarm.MainActivity
import com.example.smartalarm.R
import com.example.smartalarm.data.repository.AlarmRepositoryImpl
import com.example.smartalarm.domain.model.Alarm
import com.example.smartalarm.domain.repository.AlarmScheduler
import com.example.smartalarm.util.Constants.ACTION_ALARM_DISMISSED
import com.example.smartalarm.util.Constants.ACTION_ALARM_SNOOZE
import com.example.smartalarm.util.Constants.ACTION_ALARM_STOPPED
import com.example.smartalarm.util.Constants.ACTION_CANCEL_ALARM
import com.example.smartalarm.util.Constants.ACTION_RESCHEDULE_ALL_ALARMS
import com.example.smartalarm.util.Constants.ACTION_SCHEDULE_ALARM
import com.example.smartalarm.util.Constants.ALARM_CHANNEL_ID
import com.example.smartalarm.util.Constants.EXTRA_ALARM
import com.example.smartalarm.util.Constants.EXTRA_ALARM_ID
import com.example.smartalarm.util.Constants.EXTRA_ALARM_SNOOZE_DURATION
import com.example.smartalarm.util.Constants.EXTRA_ALARM_TIME
import com.example.smartalarm.util.Constants.EXTRA_ALARM_TYPE
import com.example.smartalarm.util.DateTimeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Broadcast receiver for handling alarm triggers and scheduling.
 */
@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmScheduler: AlarmScheduler
    
    @Inject
    lateinit var alarmRepository: AlarmRepositoryImpl
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            // Handle alarm trigger
            Intent.ACTION_BOOT_COMPLETED -> {
                Timber.d("Boot completed, rescheduling all alarms")
                CoroutineScope(Dispatchers.IO).launch {
                    alarmScheduler.rescheduleAllAlarms()
                }
            }
            
            // Schedule a new alarm
            ACTION_SCHEDULE_ALARM -> {
                val alarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(EXTRA_ALARM, Alarm::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(EXTRA_ALARM)
                }
                
                if (alarm != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val triggerTime = LocalDateTime.now().plusMinutes(1) // Default to 1 minute from now
                            alarmScheduler.scheduleAlarm(alarm, triggerTime)
                            Timber.d("Scheduled alarm: ${alarm.id} for $triggerTime")
                        } catch (e: Exception) {
                            Timber.e(e, "Failed to schedule alarm: ${alarm.id}")
                        }
                    }
                }
            }
            
            // Cancel an existing alarm
            ACTION_CANCEL_ALARM -> {
                val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1L)
                if (alarmId != -1L) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val alarm = alarmRepository.getAlarmById(alarmId)
                            if (alarm != null) {
                                alarmScheduler.cancelAlarm(alarm)
                                Timber.d("Cancelled alarm: $alarmId")
                            }
                        } catch (e: Exception) {
                            Timber.e(e, "Failed to cancel alarm: $alarmId")
                        }
                    }
                }
            }
            
            // Handle alarm trigger
            ACTION_ALARM_TRIGGERED -> {
                val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1L)
                if (alarmId != -1L) {
                    handleAlarmTrigger(context, alarmId)
                }
            }
            
            // Handle alarm dismiss
            ACTION_ALARM_DISMISSED -> {
                val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1L)
                if (alarmId != -1L) {
                    // Stop the alarm sound/vibration
                    stopAlarmSound(context)
                    
                    // If this is a repeating alarm, schedule the next occurrence
                    CoroutineScope(Dispatchers.IO).launch {
                        val alarm = alarmRepository.getAlarmById(alarmId)
                        if (alarm != null && alarm.days.isNotEmpty()) {
                            alarmScheduler.scheduleNextRepeatingAlarm(alarm)
                        }
                    }
                }
            }
            
            // Handle alarm snooze
            ACTION_ALARM_SNOOZE -> {
                val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1L)
                val snoozeMinutes = intent.getLongExtra(EXTRA_ALARM_SNOOZE_DURATION, 10L)
                
                if (alarmId != -1L) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val alarm = alarmRepository.getAlarmById(alarmId)
                        if (alarm != null) {
                            val snoozeTime = LocalDateTime.now().plusMinutes(snoozeMinutes)
                            alarmScheduler.scheduleAlarm(
                                alarm.copy(
                                    isSnoozed = true,
                                    snoozedUntil = snoozeTime
                                ),
                                snoozeTime
                            )
                            Timber.d("Snoozed alarm: $alarmId until $snoozeTime")
                        }
                    }
                }
            }
            
            // Handle alarm stop (user manually stopped the alarm)
            ACTION_ALARM_STOPPED -> {
                val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1L)
                if (alarmId != -1L) {
                    // Stop the alarm sound/vibration
                    stopAlarmSound(context)
                    
                    // Cancel any pending alarm for this ID
                    CoroutineScope(Dispatchers.IO).launch {
                        val alarm = alarmRepository.getAlarmById(alarmId)
                        if (alarm != null) {
                            alarmScheduler.cancelAlarm(alarm)
                            
                            // If this is a one-time alarm, disable it
                            if (alarm.days.isEmpty()) {
                                alarmRepository.updateAlarm(alarm.copy(isEnabled = false))
                            } else if (alarm.isSnoozed) {
                                // If this was a snoozed alarm, reset the snooze state
                                alarmRepository.updateAlarm(alarm.copy(
                                    isSnoozed = false,
                                    snoozedUntil = null
                                ))
                            }
                        }
                    }
                }
            }
            
            // Reschedule all alarms (after time/date change or app update)
            ACTION_RESCHEDULE_ALL_ALARMS -> {
                Timber.d("Rescheduling all alarms")
                CoroutineScope(Dispatchers.IO).launch {
                    alarmScheduler.rescheduleAllAlarms()
                }
            }
        }
    }
    
    private fun handleAlarmTrigger(context: Context, alarmId: Long) {
        Timber.d("Alarm triggered: id=$alarmId")
        
        // Show notification
        showAlarmNotification(context, alarmId)
        
        // Play the alarm sound and vibrate
        playAlarmSound(context)
    }
    
    private fun playAlarmSound(context: Context) {
        try {
            var alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            if (alarmSound == null) {
                // Fallback to notification sound if alarm sound is not set
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            }
            
            val ringtone = RingtoneManager.getRingtone(context, alarmSound)
            ringtone.play()
            
            // Vibrate for 500ms
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(500)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error playing alarm sound")
        }
    }
    
    private fun stopAlarmSound(context: Context) {
        try {
            // Stop any playing ringtone
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val ringtone = RingtoneManager.getRingtone(context, alarmSound)
            if (ringtone.isPlaying) {
                ringtone.stop()
            }
            
            // Cancel any ongoing vibration
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.cancel()
        } catch (e: Exception) {
            Timber.e(e, "Error stopping alarm sound")
        }
    }
    
    private fun handleDismissAlarm(context: Context, alarmId: Long) {
        Timber.d("Dismissing alarm: id=$alarmId")
        
        // Stop the alarm sound and vibration
        stopAlarmSound(context)
        
        // Cancel the notification
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        )
        notificationManager?.cancel(alarmId.toInt())
        
        // Update the alarm state in the repository
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val alarm = alarmRepository.getAlarmById(alarmId)
                if (alarm != null) {
                    // If this is a one-time alarm, disable it
                    if (alarm.days.isEmpty()) {
                        alarmRepository.updateAlarm(alarm.copy(isEnabled = false))
                    } else if (alarm.isSnoozed) {
                        // If this was a snoozed alarm, reset the snooze state
                        alarmRepository.updateAlarm(alarm.copy(
                            isSnoozed = false,
                            snoozedUntil = null
                        ))
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error dismissing alarm")
            }
        }
    }
    
    private fun showAlarmNotification(context: Context, alarmId: Long) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager
        
        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ALARM_CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notification_channel_description)
                enableVibration(true)
                setShowBadge(true)
                enableLights(true)
                setVibrationPattern(longArrayOf(0, 500, 500)) // Vibrate pattern: wait 0ms, vibrate 500ms, sleep 500ms
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        // Create an intent for the notification tap action
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_ALARM_ID, alarmId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            alarmId.toInt(),
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Create dismiss intent
        val dismissIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM_DISMISSED
            putExtra(EXTRA_ALARM_ID, alarmId)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt() + 1,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Create snooze intent
        val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM_SNOOZE
            putExtra(EXTRA_ALARM_ID, alarmId)
            putExtra(EXTRA_ALARM_SNOOZE_DURATION, 10L) // 10 minutes
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt() + 2,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Build the notification
        val notification = NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText("Alarm! Time to wake up!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setOngoing(true)
            .setFullScreenIntent(pendingIntent, true)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_snooze, "Snooze", snoozePendingIntent)
            .addAction(R.drawable.ic_dismiss, "Dismiss", dismissPendingIntent)
            .setVibrate(longArrayOf(0, 500, 500)) // Vibrate pattern for pre-Oreo
            .setLights(ContextCompat.getColor(context, R.color.colorPrimary), 1000, 1000) // LED light
            .build()
        
        // Show the notification
        notificationManager.notify(alarmId.toInt(), notification)
    }
    
    companion object {
        const val ACTION_ALARM_TRIGGERED = "com.example.smartalarm.ACTION_ALARM_TRIGGERED"
        const val EXTRA_ALARM_ID = "extra_alarm_id"
        private const val ALARM_CHANNEL_ID = "alarm_channel"
        
        /**
         * Creates an intent for triggering an alarm.
         */
        fun createIntent(context: Context, alarmId: Long): Intent {
            return Intent(context, AlarmReceiver::class.java).apply {
                action = ACTION_ALARM_TRIGGERED
                putExtra(EXTRA_ALARM_ID, alarmId)
            }
        }
    }
}
