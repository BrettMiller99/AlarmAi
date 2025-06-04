package com.example.smartalarm.domain.usecase

import com.example.smartalarm.domain.model.Alarm
import com.example.smartalarm.domain.repository.AlarmRepository
import javax.inject.Inject

/**
 * Use case for toggling the enabled state of an alarm.
 */
class ToggleAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository,
    private val getAlarmUseCase: GetAlarmUseCase
) {
    suspend operator fun invoke(alarmId: Long): Boolean {
        val alarm = getAlarmUseCase(alarmId)
        return if (alarm != null) {
            repository.setAlarmEnabled(alarmId, !alarm.isEnabled)
        } else {
            false
        }
    }
}
