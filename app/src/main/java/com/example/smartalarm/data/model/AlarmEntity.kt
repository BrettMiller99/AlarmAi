package com.example.smartalarm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.smartalarm.domain.model.Alarm
import java.time.LocalTime

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val time: LocalTime,
    val days: Set<Int> = emptySet(),
    val isEnabled: Boolean = true,
    val label: String = "",
    val vibrate: Boolean = true,
    val ringtoneUri: String = "",
    val destination: String = "",
    val destinationLatLng: String = "",
    val travelMode: String = "DRIVING",
    val bufferMinutes: Int = 15,
    val smartAlarmEnabled: Boolean = false,
    val lastTriggered: Long = 0,
    val created: Long = System.currentTimeMillis()
)

fun AlarmEntity.toAlarm(): Alarm {
    return Alarm(
        id = id,
        time = time,
        days = days.map { java.time.DayOfWeek.of(it) }.toSet(),
        isEnabled = isEnabled,
        label = label,
        vibrate = vibrate,
        ringtoneUri = ringtoneUri,
        destination = destination,
        destinationLatLng = destinationLatLng,
        travelMode = Alarm.TravelMode.valueOf(travelMode),
        bufferMinutes = bufferMinutes,
        smartAlarmEnabled = smartAlarmEnabled,
        lastTriggered = lastTriggered,
        created = created
    )
}

fun Alarm.toAlarmEntity(): AlarmEntity {
    return AlarmEntity(
        id = id,
        time = time,
        days = days.map { it.value }.toSet(),
        isEnabled = isEnabled,
        label = label,
        vibrate = vibrate,
        ringtoneUri = ringtoneUri,
        destination = destination,
        destinationLatLng = destinationLatLng,
        travelMode = travelMode.name,
        bufferMinutes = bufferMinutes,
        smartAlarmEnabled = smartAlarmEnabled,
        lastTriggered = lastTriggered,
        created = created
    )
}
