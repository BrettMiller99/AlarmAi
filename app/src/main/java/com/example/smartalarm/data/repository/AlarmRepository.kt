package com.example.smartalarm.data.repository

import com.example.smartalarm.data.local.dao.AlarmDao
import com.example.smartalarm.data.mapper.toAlarm
import com.example.smartalarm.data.mapper.toAlarmEntity
import com.example.smartalarm.data.model.AlarmEntity
import com.example.smartalarm.domain.model.Alarm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepository @Inject constructor(
    private val alarmDao: AlarmDao,
    private val mapsRepository: MapsRepository,
    private val locationRepository: LocationRepository
) {
    fun getAlarms(): Flow<List<Alarm>> {
        return alarmDao.getAlarms().map { list ->
            list.map { it.toAlarm() }
        }
    }

    fun getAlarmById(id: Long): Flow<Alarm?> {
        return alarmDao.getAlarmById(id).map { it?.toAlarm() }
    }

    suspend fun saveAlarm(alarm: Alarm): Long {
        val id = if (alarm.id == 0L) {
            alarmDao.insert(alarm.toAlarmEntity())
        } else {
            alarmDao.update(alarm.toAlarmEntity())
            alarm.id
        }
        return id
    }

    suspend fun deleteAlarm(alarm: Alarm) {
        alarmDao.delete(alarm.toAlarmEntity())
    }

    suspend fun toggleAlarm(alarm: Alarm, enabled: Boolean) {
        val updatedAlarm = alarm.copy(isEnabled = enabled)
        saveAlarm(updatedAlarm)
    }

    suspend fun calculateOptimalAlarmTime(
        alarm: Alarm,
        currentLocation: com.google.android.gms.maps.model.LatLng
    ): Alarm {
        if (!alarm.smartAlarmEnabled || alarm.destinationLatLng.isEmpty()) {
            return alarm
        }

        try {
            val destinationLatLng = alarm.destinationLatLng.split(",")
            if (destinationLatLng.size != 2) return alarm

            val destination = com.google.android.gms.maps.model.LatLng(
                destinationLatLng[0].toDouble(),
                destinationLatLng[1].toDouble()
            )

            val travelTimeMinutes = when (val result = mapsRepository.getTravelTime(
                origin = currentLocation,
                destination = destination,
                travelMode = alarm.travelMode
            )) {
                is Result.Success -> result.data
                is Result.Error -> return alarm
            }

            // Add buffer time
            val totalMinutes = travelTimeMinutes + alarm.bufferMinutes
            
            // Calculate new alarm time
            val newTime = alarm.time.minusMinutes(totalMinutes.toLong())
            
            return alarm.copy(time = newTime)
            
        } catch (e: Exception) {
            e.printStackTrace()
            return alarm
        }
    }

    suspend fun updateAlarmWithLocation(alarm: Alarm): Alarm {
        if (!alarm.smartAlarmEnabled || alarm.destinationLatLng.isNotEmpty()) {
            return alarm
        }

        return try {
            val location = locationRepository.getCurrentLocation()
            val currentLatLng = com.google.android.gms.maps.model.LatLng(
                location.latitude,
                location.longitude
            )
            calculateOptimalAlarmTime(alarm, currentLatLng)
        } catch (e: Exception) {
            e.printStackTrace()
            alarm
        }
    }
}
