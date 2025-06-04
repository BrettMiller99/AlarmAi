package com.example.smartalarm.domain.usecase

import com.example.smartalarm.domain.model.Alarm
import com.example.smartalarm.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting a specific alarm by ID.
 */
class GetAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository
) {
    operator fun invoke(id: Long): Flow<Alarm?> {
        return repository.observeAlarmById(id)
    }
}
