package com.example.smartalarm.domain.usecase

import com.example.smartalarm.domain.model.Alarm
import com.example.smartalarm.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting all alarms.
 */
class GetAlarmsUseCase @Inject constructor(
    private val repository: AlarmRepository
) {
    operator fun invoke(): Flow<List<Alarm>> {
        return repository.getAllAlarms()
    }
}
