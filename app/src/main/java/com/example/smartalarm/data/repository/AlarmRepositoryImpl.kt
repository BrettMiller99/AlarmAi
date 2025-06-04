package com.example.smartalarm.data.repository

import com.example.smartalarm.data.local.dao.AlarmDao
import com.example.smartalarm.data.local.entity.AlarmEntity
import com.example.smartalarm.data.local.entity.toDomain
import com.example.smartalarm.data.local.entity.toEntity
import com.example.smartalarm.domain.model.Alarm
import com.example.smartalarm.domain.repository.AlarmRepository
import com.example.smartalarm.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [AlarmRepository] that uses Room database for data persistence.
 * This class handles all data operations for alarms, including CRUD operations and queries.
 */
@Singleton
class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao
) : AlarmRepository {
    
    // Basic CRUD operations
    
    override fun getAlarms(): Flow<List<Alarm>> {
        return alarmDao.getAlarms().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getAlarmById(id: Long): Alarm? {
        return alarmDao.getAlarmById(id)?.toDomain()
    }
    
    override suspend fun insertAlarm(alarm: Alarm): Long {
        return alarmDao.insertAlarm(alarm.toEntity())
    }
    
    override suspend fun updateAlarm(alarm: Alarm) {
        alarmDao.updateAlarm(alarm.toEntity())
    }
    
    override suspend fun deleteAlarm(alarm: Alarm) {
        alarmDao.deleteAlarm(alarm.toEntity())
    }
    
    override suspend fun deleteAlarmById(id: Long) {
        alarmDao.deleteAlarmById(id)
    }
    
    override suspend fun deleteAllAlarms() {
        alarmDao.deleteAllAlarms()
    }
    
    // Alarm state management
    
    override suspend fun setAlarmEnabled(id: Long, isEnabled: Boolean): Boolean {
        return try {
            alarmDao.updateAlarmEnabledState(id, isEnabled)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun toggleAlarm(id: Long): Boolean? {
        val alarm = getAlarmById(id) ?: return null
        val newState = !alarm.isEnabled
        return if (setAlarmEnabled(id, newState)) {
            newState
        } else {
            null
        }
    }
    
    override suspend fun markAlarmTriggered(id: Long): Boolean {
        return try {
            alarmDao.updateLastTriggered(id)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    // Query operations
    
    override fun getEnabledAlarms(): Flow<List<Alarm>> {
        return alarmDao.getEnabledAlarms().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getEnabledSmartAlarms(): Flow<List<Alarm>> {
        return alarmDao.getEnabledSmartAlarms().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getAlarmsForTime(time: LocalTime, dayOfWeek: Int): List<Alarm> {
        return alarmDao.getAlarmsForTime(time, dayOfWeek).map { it.toDomain() }
    }
    
    override suspend fun getNextAlarm(currentTime: LocalTime, currentDayOfWeek: Int): Alarm? {
        return alarmDao.getNextAlarm(currentTime, currentDayOfWeek)?.toDomain()
    }
    
    override suspend fun getAlarmCount(): Int {
        return alarmDao.getAlarmCount()
    }
    
    override suspend fun getEnabledAlarmCount(): Int {
        return alarmDao.getEnabledAlarmCount()
    }
    
    // Batch operations
    
    override suspend fun deleteDisabledAlarms(): Int {
        return try {
            val countBefore = alarmDao.getAlarmCount()
            alarmDao.deleteDisabledAlarms()
            val countAfter = alarmDao.getAlarmCount()
            countBefore - countAfter
        } catch (e: Exception) {
            0
        }
    }
    
    override suspend fun deleteTriggeredOneTimeAlarms(): Int {
        return try {
            val countBefore = alarmDao.getAlarmCount()
            alarmDao.deleteTriggeredOneTimeAlarms()
            val countAfter = alarmDao.getAlarmCount()
            countBefore - countAfter
        } catch (e: Exception) {
            0
        }
    }
    
    // Smart alarm operations
    
    override suspend fun updateSmartAlarmSettings(
        id: Long,
        isSmart: Boolean,
        destination: String,
        travelMode: String,
        bufferMinutes: Int
    ): Boolean {
        return try {
            val alarm = getAlarmById(id) ?: return false
            val updatedAlarm = alarm.copy(
                isSmart = isSmart,
                destination = if (isSmart) destination else "",
                travelMode = if (isSmart) travelMode else Constants.DEFAULT_TRAVEL_MODE,
                bufferMinutes = if (isSmart) bufferMinutes.coerceIn(
                    Constants.MIN_BUFFER_MINUTES,
                    Constants.MAX_BUFFER_MINUTES
                ) else 0
            )
            updateAlarm(updatedAlarm)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun getAlarmsNeedingTrafficUpdates(): List<Alarm> {
        return alarmDao.getEnabledSmartAlarms()
            .map { entities -> entities.map { it.toDomain() } }
            .first()
            .filter { it.isSmart && it.destination.isNotBlank() }
    }
    
    // Helper methods
    
    private suspend fun updateLastTriggered(id: Long) {
        alarmDao.updateLastTriggered(id)
    }
}
