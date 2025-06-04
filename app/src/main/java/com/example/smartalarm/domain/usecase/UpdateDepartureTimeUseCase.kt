package com.example.smartalarm.domain.usecase

import com.example.smartalarm.domain.repository.AlarmRepository
import java.time.LocalTime
import javax.inject.Inject

/**
 * Use case for updating the departure time of an alarm.
 */
class UpdateDepartureTimeUseCase @Inject constructor(
    private val repository: AlarmRepository
) {
    suspend operator fun invoke(alarmId: Long, departureTime: LocalTime) {
        repository.updateDepartureTime(alarmId, departureTime)
    }
}
