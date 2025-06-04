package com.example.smartalarm.ui.alarms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.data.repository.AlarmRepository
import com.example.smartalarm.domain.model.Alarm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AlarmUiState>(AlarmUiState.Loading)
    val uiState: StateFlow<AlarmUiState> = _uiState

    private val _editAlarmState = MutableStateFlow<EditAlarmState?>(null)
    val editAlarmState: StateFlow<EditAlarmState?> = _editAlarmState

    init {
        loadAlarms()
    }

    private fun loadAlarms() {
        viewModelScope.launch {
            alarmRepository.getAlarms().collectLatest { alarms ->
                _uiState.value = AlarmUiState.Success(alarms.sortedBy { it.time })
            }
        }
    }

    fun toggleAlarm(alarm: Alarm, enabled: Boolean) {
        viewModelScope.launch {
            alarmRepository.toggleAlarm(alarm, enabled)
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            alarmRepository.deleteAlarm(alarm)
        }
    }

    fun startEditingAlarm(alarm: Alarm? = null) {
        _editAlarmState.value = EditAlarmState(
            id = alarm?.id ?: 0,
            time = alarm?.time ?: LocalTime.now().plusMinutes(1),
            label = alarm?.label ?: "",
            days = alarm?.days ?: emptySet(),
            vibrate = alarm?.vibrate ?: true,
            smartAlarmEnabled = alarm?.smartAlarmEnabled ?: false,
            destination = alarm?.destination ?: "",
            travelMode = alarm?.travelMode ?: Alarm.TravelMode.DRIVING,
            bufferMinutes = alarm?.bufferMinutes ?: 15,
            isNew = alarm == null
        )
    }

    fun updateEditState(update: (EditAlarmState) -> EditAlarmState) {
        _editAlarmState.value = _editAlarmState.value?.let(update)
    }

    fun saveAlarm() {
        val state = _editAlarmState.value ?: return
        
        viewModelScope.launch {
            val alarm = Alarm(
                id = state.id,
                time = state.time,
                days = state.days,
                isEnabled = true,
                label = state.label,
                vibrate = state.vibrate,
                smartAlarmEnabled = state.smartAlarmEnabled,
                destination = state.destination,
                destinationLatLng = "", // Will be set after geocoding
                travelMode = state.travelMode,
                bufferMinutes = state.bufferMinutes
            )
            
            alarmRepository.saveAlarm(alarm)
            _editAlarmState.value = null
        }
    }

    fun cancelEditing() {
        _editAlarmState.value = null
    }
}

sealed class AlarmUiState {
    object Loading : AlarmUiState()
    data class Success(val alarms: List<Alarm>) : AlarmUiState()
    data class Error(val message: String) : AlarmUiState()
}

data class EditAlarmState(
    val id: Long,
    val time: LocalTime,
    val label: String,
    val days: Set<Int>,
    val vibrate: Boolean,
    val smartAlarmEnabled: Boolean,
    val destination: String,
    val travelMode: Alarm.TravelMode,
    val bufferMinutes: Int,
    val isNew: Boolean
)
