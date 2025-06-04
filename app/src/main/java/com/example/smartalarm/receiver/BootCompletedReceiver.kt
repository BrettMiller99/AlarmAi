package com.example.smartalarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.smartalarm.service.AlarmService
import com.example.smartalarm.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || 
            intent.action == "android.intent.action.QUICKBOOT_POWERON" ||
            intent.action == "com.htc.intent.action.QUICKBOOT_POWERON") {
            
            // Start the alarm service to reschedule all alarms
            val serviceIntent = Intent(context, AlarmService::class.java).apply {
                action = Constants.ACTION_RESCHEDULE_ALL_ALARMS
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
                    alarmScheduler.rescheduleAllAlarms()
                } catch (e: Exception) {
                    // Handle any errors
                    e.printStackTrace()
                }
            }
        }
    }
}
