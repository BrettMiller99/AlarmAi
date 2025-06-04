package com.example.smartalarm.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.smartalarm.R
import com.example.smartalarm.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmService : Service() {

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == null) return START_NOT_STICKY

        when (intent.action) {
            Constants.ACTION_SCHEDULE_ALARM -> {
                val alarm = intent.getParcelableExtra<Alarm>(Constants.EXTRA_ALARM)
                alarm?.let { alarmScheduler.schedule(it) }
            }
            Constants.ACTION_CANCEL_ALARM -> {
                val alarm = intent.getParcelableExtra<Alarm>(Constants.EXTRA_ALARM)
                alarm?.let { alarmScheduler.cancel(it) }
            }
            Constants.ACTION_RESCHEDULE_ALL_ALARMS -> {
                // This will be called after device boot
                startForegroundService()
                alarmScheduler.rescheduleAllAlarms()
            }
        }

        return START_STICKY
    }

    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Alarm Service")
            .setContentText("Managing your alarms")
            .setSmallIcon(R.drawable.ic_alarm)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }
}
