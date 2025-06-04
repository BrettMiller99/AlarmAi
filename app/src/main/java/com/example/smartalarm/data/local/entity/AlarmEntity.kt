package com.example.smartalarm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.smartalarm.domain.model.Alarm
import com.example.smartalarm.util.Constants
import java.time.DayOfWeek
import java.time.LocalTime

/**
 * Entity class representing an alarm in the Room database.
 * This class maps between the domain model [Alarm] and the database representation.
 *
 * @property id Unique identifier for the alarm
 * @property time The time when the alarm should go off
 * @property days Set of days (as integers 1-7) when the alarm should repeat
 * @property isEnabled Whether the alarm is currently active
 * @property label User-defined name for the alarm
 * @property isVibrate Whether the alarm should vibrate
 * @property soundUri URI of the sound to play when the alarm goes off
 * @property volume Volume level for the alarm sound (0-100)
 * @property isSmart Whether this is a smart alarm that adjusts based on traffic
 * @property destination Destination address for smart alarm
 * @property travelMode Travel mode for smart alarm (driving, walking, etc.)
 * @property bufferMinutes Additional minutes to add to the calculated travel time
 * @property lastTriggered When the alarm was last triggered
 * @property created When the alarm was created
 * @property modified When the alarm was last modified
 */
@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val time: LocalTime = LocalTime.now(),
    val days: Set<Int> = emptySet(),
    val isEnabled: Boolean = true,
    val label: String = Constants.DEFAULT_ALARM_NAME,
    val isVibrate: Boolean = true,
    val soundUri: String = "",
    val volume: Int = Constants.DEFAULT_ALARM_VOLUME,
    val isSmart: Boolean = false,
    val destination: String = "",
    val travelMode: String = Constants.DEFAULT_TRAVEL_MODE,
    val bufferMinutes: Int = Constants.DEFAULT_ALARM_BUFFER_MINUTES,
    val lastTriggered: Long = 0,
    val created: Long = System.currentTimeMillis(),
    val modified: Long = System.currentTimeMillis()
) {
    
    companion object {
        /**
         * Converts a domain [Alarm] to an [AlarmEntity] for database storage.
         */
        fun fromDomain(alarm: Alarm): AlarmEntity {
            return AlarmEntity(
                id = alarm.id,
                time = alarm.time,
                days = alarm.days.map { it.value }.toSet(),
                isEnabled = alarm.isEnabled,
                label = alarm.label,
                isVibrate = alarm.isVibrate,
                soundUri = alarm.soundUri,
                volume = alarm.volume,
                isSmart = alarm.isSmart,
                destination = alarm.destination,
                travelMode = alarm.travelMode,
                bufferMinutes = alarm.bufferMinutes,
                lastTriggered = alarm.lastTriggered,
                created = alarm.created,
                modified = alarm.modified
            )
        }
    }
    
    /**
     * Converts this [AlarmEntity] to a domain [Alarm] model.
     */
    fun toDomain(): Alarm {
        return Alarm(
            id = id,
            time = time,
            days = days.map { DayOfWeek.of(it) }.toSet(),
            isEnabled = isEnabled,
            label = label,
            isVibrate = isVibrate,
            soundUri = soundUri,
            volume = volume,
            isSmart = isSmart,
            destination = destination,
            travelMode = travelMode,
            bufferMinutes = bufferMinutes,
            lastTriggered = lastTriggered,
            created = created,
            modified = modified
        )
    }
    
    /**
     * Creates a copy of this entity with the specified enabled state.
     */
    fun copyWithEnabled(enabled: Boolean): AlarmEntity {
        return copy(isEnabled = enabled, modified = System.currentTimeMillis())
    }
    
    /**
     * Creates a copy of this entity with the last triggered time updated to now.
     */
    fun copyWithTriggeredNow(): AlarmEntity {
        val now = System.currentTimeMillis()
        return copy(lastTriggered = now, modified = now)
    }
    
    /**
     * Creates a copy of this entity with the specified time.
     */
    fun copyWithTime(hour: Int, minute: Int): AlarmEntity {
        return copy(
            time = LocalTime.of(hour, minute),
            modified = System.currentTimeMillis()
        )
    }
    
    /**
     * Creates a copy of this entity with the specified days.
     */
    fun copyWithDays(days: Set<Int>): AlarmEntity {
        return copy(days = days, modified = System.currentTimeMillis())
    }
    
    /**
     * Creates a copy of this entity with the specified label.
     */
    fun copyWithLabel(label: String): AlarmEntity {
        return copy(
            label = label.ifEmpty { Constants.DEFAULT_ALARM_NAME },
            modified = System.currentTimeMillis()
        )
    }
    
    /**
     * Creates a copy of this entity with the specified sound URI.
     */
    fun copyWithSound(soundUri: String): AlarmEntity {
        return copy(soundUri = soundUri, modified = System.currentTimeMillis())
    }
    
    /**
     * Creates a copy of this entity with the specified volume.
     */
    fun copyWithVolume(volume: Int): AlarmEntity {
        return copy(
            volume = volume.coerceIn(0, 100),
            modified = System.currentTimeMillis()
        )
    }
    
    /**
     * Creates a copy of this entity with the specified smart alarm settings.
     */
    fun copyWithSmartSettings(
        isSmart: Boolean = this.isSmart,
        destination: String = this.destination,
        travelMode: String = this.travelMode,
        bufferMinutes: Int = this.bufferMinutes
    ): AlarmEntity {
        return copy(
            isSmart = isSmart,
            destination = if (isSmart) destination else "",
            travelMode = if (isSmart) travelMode else Constants.DEFAULT_TRAVEL_MODE,
            bufferMinutes = if (isSmart) bufferMinutes.coerceIn(
                Constants.MIN_BUFFER_MINUTES,
                Constants.MAX_BUFFER_MINUTES
            ) else 0,
            modified = System.currentTimeMillis()
        )
    }
}
