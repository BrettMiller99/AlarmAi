package com.example.smartalarm.data.repository

import com.example.smartalarm.data.local.dao.AlarmDao
import com.example.smartalarm.data.mapper.toAlarm
import com.example.smartalarm.data.mapper.toAlarmEntity
import com.example.smartalarm.domain.model.Alarm
import com.example.smartalarm.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import javax.inject.Inject

/**
 * Implementation of [AlarmRepository] that uses Room database as the data source.
 */
class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao
) : AlarmRepository {
    
    override fun getAllAlarms(): Flow<List<Alarm>> {
        return alarmDao.observeAll()
    }
    
    override fun getEnabledAlarms(): Flow<List<Alarm>> {
        return alarmDao.observeEnabledAlarms()
    }
    
    override suspend fun getAlarmById(id: Long): Alarm? {
        return alarmDao.getById(id)
    }
    
    override fun observeAlarmById(id: Long): Flow<Alarm?> {
        return alarmDao.observeById(id)
    }
    
    override suspend fun insertAlarm(alarm: Alarm): Long {
        return alarmDao.insert(alarm)
    }
    
    override suspend fun updateAlarm(alarm: Alarm): Boolean {
        return alarmDao.update(alarm) > 0
    }
    
    override suspend fun deleteAlarm(alarm: Alarm): Boolean {
        return alarmDao.delete(alarm) > 0
    }
    
    override suspend fun deleteAlarmById(id: Long): Boolean {
        return alarmDao.deleteById(id) > 0
    }
    
    override suspend fun updateDepartureTime(alarmId: Long, departureTime: LocalTime) {
        alarmDao.updateDepartureTime(alarmId, departureTime)
    }
    
    override suspend fun setAlarmEnabled(alarmId: Long, isEnabled: Boolean): Boolean {
        return alarmDao.setEnabled(alarmId, isEnabled) > 0
    }
}
