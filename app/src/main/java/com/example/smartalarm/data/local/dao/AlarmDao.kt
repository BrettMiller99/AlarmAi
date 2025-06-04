package com.example.smartalarm.data.local.dao

import androidx.room.*
import com.example.smartalarm.data.local.entity.AlarmEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

/**
 * Data Access Object for [AlarmEntity] operations.
 * Provides methods to access and manage alarm data in the database.
 */
@Dao
interface AlarmDao {
    /**
     * Get all alarms, ordered by time
     */
    @Query("SELECT * FROM alarms ORDER BY time ASC")
    fun getAlarms(): Flow<List<AlarmEntity>>
    
    /**
     * Get a specific alarm by its ID
     */
    @Query("SELECT * FROM alarms WHERE id = :id")
    suspend fun getAlarmById(id: Long): AlarmEntity?
    
    /**
     * Insert a new alarm
     * @return The ID of the inserted alarm
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmEntity): Long
    
    /**
     * Update an existing alarm
     */
    @Update
    suspend fun updateAlarm(alarm: AlarmEntity)
    
    /**
     * Delete an alarm
     */
    @Delete
    suspend fun deleteAlarm(alarm: AlarmEntity)
    
    /**
     * Delete an alarm by its ID
     */
    @Query("DELETE FROM alarms WHERE id = :id")
    suspend fun deleteAlarmById(id: Long)
    
    /**
     * Update the last calculated departure time for an alarm.
     */
    @Query("UPDATE alarms SET lastCalculatedDepartureTime = :departureTime, updatedTimestamp = :timestamp WHERE id = :id")
    suspend fun updateDepartureTime(id: Long, departureTime: LocalTime, timestamp: Long = System.currentTimeMillis())
    
    /**
     * Toggle the enabled state of an alarm.
     * @return The number of rows updated.
     */
    @Query("UPDATE alarms SET isEnabled = :isEnabled, updatedTimestamp = :timestamp WHERE id = :id")
    suspend fun setEnabled(id: Long, isEnabled: Boolean, timestamp: Long = System.currentTimeMillis()): Int
    
    /**
     * Update the enabled state of an alarm
     */
    @Query("UPDATE alarms SET isEnabled = :isEnabled, modified = :timestamp WHERE id = :id")
    suspend fun updateAlarmEnabledState(id: Long, isEnabled: Boolean, timestamp: Long = System.currentTimeMillis())
    
    /**
     * Get all enabled alarms
     */
    @Query("SELECT * FROM alarms WHERE isEnabled = 1 ORDER BY time ASC")
    fun getEnabledAlarms(): Flow<List<AlarmEntity>>
    
    /**
     * Get all enabled smart alarms
     */
    @Query("SELECT * FROM alarms WHERE isEnabled = 1 AND isSmart = 1 ORDER BY time ASC")
    fun getEnabledSmartAlarms(): Flow<List<AlarmEntity>>
    
    /**
     * Get alarms that should trigger at the specified time
     */
    @Query(""""
        SELECT * FROM alarms 
        WHERE isEnabled = 1 
        AND time = :time 
        AND (
            :dayOfWeek IN (SELECT value FROM json_each(days)) 
            OR json_array_length(days) = 0
        )
    """)
    suspend fun getAlarmsForTime(time: LocalTime, dayOfWeek: Int): List<AlarmEntity>
    
    /**
     * Get the next alarm to trigger
     */
    @Query(""""
        SELECT * FROM alarms 
        WHERE isEnabled = 1 
        AND (
            time > :currentTime 
            OR EXISTS (
                SELECT 1 FROM json_each(days) 
                WHERE value > :currentDayOfWeek 
                AND value > 0
            )
        )
        ORDER BY 
            CASE 
                WHEN time > :currentTime AND :currentDayOfWeek IN (SELECT value FROM json_each(days)) THEN 0
                WHEN time <= :currentTime AND EXISTS (SELECT 1 FROM json_each(days) WHERE value > :currentDayOfWeek) THEN 1
                ELSE 2
            END,
            time ASC
        LIMIT 1
    """)
    suspend fun getNextAlarm(currentTime: LocalTime, currentDayOfWeek: Int): AlarmEntity?
    
    /**
     * Update the last triggered time of an alarm
     */
    @Query("UPDATE alarms SET lastTriggered = :timestamp, modified = :timestamp WHERE id = :id")
    suspend fun updateLastTriggered(id: Long, timestamp: Long = System.currentTimeMillis())
    
    /**
     * Get the count of all alarms
     */
    @Query("SELECT COUNT(*) FROM alarms")
    suspend fun getAlarmCount(): Int
    
    /**
     * Get the count of enabled alarms
     */
    @Query("SELECT COUNT(*) FROM alarms WHERE isEnabled = 1")
    suspend fun getEnabledAlarmCount(): Int
    
    /**
     * Delete all alarms
     */
    @Query("DELETE FROM alarms")
    suspend fun deleteAllAlarms()
    
    /**
     * Delete all disabled alarms
     */
    @Query("DELETE FROM alarms WHERE isEnabled = 0")
    suspend fun deleteDisabledAlarms()
    
    /**
     * Delete all one-time alarms that have already triggered
     */
    @Query(""""
        DELETE FROM alarms 
        WHERE isEnabled = 0 
        AND json_array_length(days) = 0
        AND lastTriggered > 0
    """)
    suspend fun deleteTriggeredOneTimeAlarms()
}
