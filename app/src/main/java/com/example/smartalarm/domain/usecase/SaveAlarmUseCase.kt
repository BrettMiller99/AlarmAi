package com.example.smartalarm.domain.usecase

import com.example.smartalarm.domain.model.Alarm
import com.example.smartalarm.domain.repository.AlarmRepository
import javax.inject.Inject

/**
 * Use case for saving an alarm (both insert and update).
 */
class SaveAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository
) {
    suspend operator fun invoke(alarm: Alarm): Long {
        return if (alarm.id == 0L) {
            // New alarm
            repository.insertAlarm(alarm)
        } else {
            // Update existing alarm
            if (repository.updateAlarm(alarm)) {
                alarm.id
            } else {
                // If update fails (e.g., alarm was deleted), insert as new
                repository.insertAlarm(alarm.copy(id = 0))
            }
        }
    }
}
