package com.example.smartalarm.domain.usecase

import com.example.smartalarm.domain.repository.AlarmRepository
import javax.inject.Inject

/**
 * Use case for deleting an alarm.
 */
class DeleteAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository
) {
    suspend operator fun invoke(alarmId: Long): Boolean {
        return repository.deleteAlarmById(alarmId)
    }
}
