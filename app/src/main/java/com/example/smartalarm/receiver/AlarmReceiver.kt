package com.example.smartalarm.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.smartalarm.MainActivity
import com.example.smartalarm.R
import com.example.smartalarm.domain.model.Alarm
import com.example.smartalarm.util.DateTimeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Broadcast receiver for handling alarm triggers.
 */
@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmScheduler: com.example.smartalarm.util.AlarmScheduler
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            // Handle alarm trigger
            ACTION_ALARM_TRIGGERED -> {
                val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1L)
                if (alarmId != -1L) {
                    handleAlarmTrigger(context, alarmId)
                }
            }
            
            // Handle boot completed to reschedule alarms
            Intent.ACTION_BOOT_COMPLETED -> {
                // We'll implement this later when we have the repository set up
                Timber.d("Boot completed, will reschedule alarms")
            }
        }
    }
    
    private fun handleAlarmTrigger(context: Context, alarmId: Long) {
        Timber.d("Alarm triggered: id=$alarmId")
        
        // In a real app, we would fetch the alarm details from the database
        // For now, we'll just show a notification
        showAlarmNotification(context, alarmId)
        
        // Play the alarm sound and vibrate
        playAlarmSound(context)
        
        // If this is a one-time alarm, we should disable it
        // For recurring alarms, the next occurrence will be scheduled when the alarm is dismissed
        if (/* alarm is not recurring */ true) {
            // Disable the alarm in the database
            CoroutineScope(Dispatchers.IO).launch {
                // repository.setAlarmEnabled(alarmId, false)
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
        
        // Build the notification
        val notification = NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm) // You'll need to add this drawable
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText("Alarm! Time to wake up!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setOngoing(true)
            .setFullScreenIntent(pendingIntent, true)
            .setContentIntent(pendingIntent)
            .build()
        
        // Show the notification
        notificationManager.notify(alarmId.toInt(), notification)
    }
    
    private fun playAlarmSound(context: Context) {
        try {
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            
            val ringtone = RingtoneManager.getRingtone(context, alarmSound)
            ringtone.play()
        } catch (e: Exception) {
            Timber.e(e, "Failed to play alarm sound")
        }
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
