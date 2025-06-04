package com.example.smartalarm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.smartalarm.domain.model.Alarm
import java.time.LocalTime

/**
 * Room entity for the alarms table.
 * This is the database representation of an [Alarm].
 */
@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val isEnabled: Boolean = true,
    val targetArrivalTime: LocalTime,
    val travelBufferMinutes: Int = 15,
    val destinationAddress: String,
    val destinationLat: Double? = null,
    val destinationLng: Double? = null,
    val lastCalculatedDepartureTime: LocalTime? = null,
    val isRecurring: Boolean = false,
    val daysOfWeek: Set<Int> = emptySet(),
    val isVibrate: Boolean = true,
    val ringtoneUri: String = "",
    val volume: Float = 0.8f,
    val isIncreasingVolume: Boolean = true,
    val createdTimestamp: Long = System.currentTimeMillis(),
    val updatedTimestamp: Long = System.currentTimeMillis()
)
