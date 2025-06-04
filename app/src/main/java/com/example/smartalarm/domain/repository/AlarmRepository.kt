package com.example.smartalarm.domain.repository

import com.example.smartalarm.domain.model.Alarm
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

/**
 * Repository interface for alarm operations.
 */
interface AlarmRepository {
    /**
     * Get all alarms, ordered by creation time (newest first).
     */
    fun getAllAlarms(): Flow<List<Alarm>>
    
    /**
     * Get all enabled alarms.
     */
    fun getEnabledAlarms(): Flow<List<Alarm>>
    
    /**
     * Get a specific alarm by ID.
     */
    suspend fun getAlarmById(id: Long): Alarm?
    
    /**
     * Observe changes to a specific alarm by ID.
     */
    fun observeAlarmById(id: Long): Flow<Alarm?>
    
    /**
     * Insert a new alarm.
     * @return The ID of the newly inserted alarm.
     */
    suspend fun insertAlarm(alarm: Alarm): Long
    
    /**
     * Update an existing alarm.
     * @return true if the update was successful.
     */
    suspend fun updateAlarm(alarm: Alarm): Boolean
    
    /**
     * Delete an alarm.
     * @return true if the deletion was successful.
     */
    suspend fun deleteAlarm(alarm: Alarm): Boolean
    
    /**
     * Delete an alarm by ID.
     * @return true if the deletion was successful.
     */
    suspend fun deleteAlarmById(id: Long): Boolean
    
    /**
     * Update the last calculated departure time for an alarm.
     */
    suspend fun updateDepartureTime(alarmId: Long, departureTime: LocalTime)
    
    /**
     * Toggle the enabled state of an alarm.
     * @return true if the update was successful.
     */
    suspend fun setAlarmEnabled(alarmId: Long, isEnabled: Boolean): Boolean
}
