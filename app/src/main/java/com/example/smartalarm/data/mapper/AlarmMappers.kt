package com.example.smartalarm.data.mapper

import com.example.smartalarm.data.local.entity.AlarmEntity
import com.example.smartalarm.domain.model.Alarm

/**
 * Converts an [AlarmEntity] to an [Alarm] domain model.
 */
fun AlarmEntity.toAlarm(): Alarm {
    return Alarm(
        id = id,
        name = name,
        isEnabled = isEnabled,
        targetArrivalTime = targetArrivalTime,
        travelBufferMinutes = travelBufferMinutes,
        destinationAddress = destinationAddress,
        destinationLat = destinationLat,
        destinationLng = destinationLng,
        lastCalculatedDepartureTime = lastCalculatedDepartureTime,
        isRecurring = isRecurring,
        daysOfWeek = daysOfWeek,
        isVibrate = isVibrate,
        ringtoneUri = ringtoneUri,
        volume = volume,
        isIncreasingVolume = isIncreasingVolume,
        createdTimestamp = createdTimestamp,
        updatedTimestamp = updatedTimestamp
    )
}

/**
 * Converts an [Alarm] domain model to an [AlarmEntity].
 */
fun Alarm.toAlarmEntity(): AlarmEntity {
    return AlarmEntity(
        id = id,
        name = name,
        isEnabled = isEnabled,
        targetArrivalTime = targetArrivalTime,
        travelBufferMinutes = travelBufferMinutes,
        destinationAddress = destinationAddress,
        destinationLat = destinationLat,
        destinationLng = destinationLng,
        lastCalculatedDepartureTime = lastCalculatedDepartureTime,
        isRecurring = isRecurring,
        daysOfWeek = daysOfWeek,
        isVibrate = isVibrate,
        ringtoneUri = ringtoneUri,
        volume = volume,
        isIncreasingVolume = isIncreasingVolume,
        createdTimestamp = createdTimestamp,
        updatedTimestamp = updatedTimestamp
    )
}
