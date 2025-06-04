package com.example.smartalarm.data.local.dao

import androidx.room.*
import com.example.smartalarm.domain.model.Alarm
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

/**
 * Data Access Object for the alarms table.
 */
@Dao
interface AlarmDao {
    /**
     * Get all alarms, ordered by creation time (newest first).
     */
    @Query("SELECT * FROM alarms ORDER BY createdTimestamp DESC")
    fun observeAll(): Flow<List<Alarm>>
    
    /**
     * Get all enabled alarms.
     */
    @Query("SELECT * FROM alarms WHERE isEnabled = 1 ORDER BY createdTimestamp DESC")
    fun observeEnabledAlarms(): Flow<List<Alarm>>
    
    /**
     * Get a specific alarm by ID.
     */
    @Query("SELECT * FROM alarms WHERE id = :id")
    suspend fun getById(id: Long): Alarm?
    
    /**
     * Get a specific alarm by ID as a Flow.
     */
    @Query("SELECT * FROM alarms WHERE id = :id")
    fun observeById(id: Long): Flow<Alarm?>
    
    /**
     * Insert a new alarm.
     * @return The ID of the newly inserted alarm.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarm: Alarm): Long
    
    /**
     * Update an existing alarm.
     * @return The number of rows updated.
     */
    @Update
    suspend fun update(alarm: Alarm): Int
    
    /**
     * Delete an alarm.
     * @return The number of rows deleted.
     */
    @Delete
    suspend fun delete(alarm: Alarm): Int
    
    /**
     * Delete an alarm by ID.
     * @return The number of rows deleted.
     */
    @Query("DELETE FROM alarms WHERE id = :id")
    suspend fun deleteById(id: Long): Int
    
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
}
