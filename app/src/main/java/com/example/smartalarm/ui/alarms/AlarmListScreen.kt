package com.example.smartalarm.ui.alarms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartalarm.R
import com.example.smartalarm.domain.model.Alarm
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun AlarmListScreen(
    viewModel: AlarmViewModel = hiltViewModel(),
    onSettingsClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val editAlarmState by viewModel.editAlarmState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.startEditingAlarm() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_alarm)
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is AlarmUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is AlarmUiState.Success -> {
                    if (state.alarms.isEmpty()) {
                        EmptyAlarms()
                    } else {
                        AlarmList(
                            alarms = state.alarms,
                            onAlarmToggled = { alarm, enabled ->
                                viewModel.toggleAlarm(alarm, enabled)
                            },
                            onAlarmClick = { alarm ->
                                viewModel.startEditingAlarm(alarm)
                            }
                        )
                    }
                }
                is AlarmUiState.Error -> {
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    // Show edit dialog when editing an alarm
    editAlarmState?.let { state ->
        EditAlarmDialog(
            state = state,
            onDismiss = { viewModel.cancelEditing() },
            onConfirm = { viewModel.saveAlarm() },
            onStateChange = { viewModel.updateEditState { it.copy(
                time = it.time,
                label = it.label,
                days = it.days,
                vibrate = it.vibrate,
                smartAlarmEnabled = it.smartAlarmEnabled,
                destination = it.destination,
                travelMode = it.travelMode,
                bufferMinutes = it.bufferMinutes
            ) }}
        )
    }
}

@Composable
private fun AlarmList(
    alarms: List<Alarm>,
    onAlarmToggled: (Alarm, Boolean) -> Unit,
    onAlarmClick: (Alarm) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(
            items = alarms,
            key = { it.id }
        ) { alarm ->
            AlarmItem(
                alarm = alarm,
                onToggle = { enabled -> onAlarmToggled(alarm, enabled) },
                onClick = { onAlarmClick(alarm) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun AlarmItem(
    alarm: Alarm,
    onToggle: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    val timeFormatter = remember { DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT) }
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = alarm.time.format(timeFormatter),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Normal
                )
                if (alarm.label.isNotBlank()) {
                    Text(
                        text = alarm.label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (alarm.smartAlarmEnabled && alarm.destination.isNotBlank()) {
                    Text(
                        text = "→ ${alarm.destination}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Switch(
                checked = alarm.isEnabled,
                onCheckedChange = onToggle
            )
        }
    }
}

@Composable
private fun EmptyAlarms() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No alarms",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Tap + to add an alarm",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}
