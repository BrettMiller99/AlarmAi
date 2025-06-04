package com.example.smartalarm.domain.repository

import com.example.smartalarm.domain.model.Alarm
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

/**
 * Repository interface that defines the data operations for alarms.
 * This is the main entry point for accessing alarm data in the domain layer.
 */
interface AlarmRepository {
    // Basic CRUD operations
    
    /**
     * Get all alarms, ordered by time
     */
    fun getAlarms(): Flow<List<Alarm>>
    
    /**
     * Get a specific alarm by its ID
     * @param id The ID of the alarm to retrieve
     * @return The alarm with the specified ID, or null if not found
     */
    suspend fun getAlarmById(id: Long): Alarm?
    
    /**
     * Insert a new alarm
     * @param alarm The alarm to insert
     * @return The ID of the newly inserted alarm
     */
    suspend fun insertAlarm(alarm: Alarm): Long
    
    /**
     * Update an existing alarm
     * @param alarm The alarm with updated values
     */
    suspend fun updateAlarm(alarm: Alarm)
    
    /**
     * Delete an alarm
     * @param alarm The alarm to delete
     */
    suspend fun deleteAlarm(alarm: Alarm)
    
    /**
     * Delete an alarm by its ID
     * @param id The ID of the alarm to delete
     */
    suspend fun deleteAlarmById(id: Long)
    
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
