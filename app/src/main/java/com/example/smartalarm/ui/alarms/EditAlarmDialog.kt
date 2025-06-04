package com.example.smartalarm.ui.alarms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.smartalarm.R
import com.example.smartalarm.domain.model.Alarm
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun EditAlarmDialog(
    state: EditAlarmState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onStateChange: (EditAlarmState) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }
    
    if (showTimePicker) {
        TimePickerDialog(
            initialTime = state.time,
            onTimeSelected = { time ->
                onStateChange(state.copy(time = time))
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
        return
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (state.isNew) "Add Alarm" else "Edit Alarm",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Time Picker
                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = state.time.format(DateTimeFormatter.ofPattern("h:mm a")),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Label
                OutlinedTextField(
                    value = state.label,
                    onValueChange = { onStateChange(state.copy(label = it)) },
                    label = { Text("Label (optional)") },
                    leadingIcon = {
                        Icon(Icons.Default.Label, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Smart Alarm Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Smart Alarm",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = state.smartAlarmEnabled,
                        onCheckedChange = {
                            onStateChange(state.copy(smartAlarmEnabled = it))
                        }
                    )
                }
                
                if (state.smartAlarmEnabled) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Destination
                    OutlinedTextField(
                        value = state.destination,
                        onValueChange = { onStateChange(state.copy(destination = it)) },
                        label = { Text("Destination") },
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Travel Mode
                    Text(
                        text = "Travel Mode",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TravelModeButton(
                            icon = Icons.Default.DirectionsCar,
                            label = "Drive",
                            selected = state.travelMode == Alarm.TravelMode.DRIVING,
                            onClick = { onStateChange(state.copy(travelMode = Alarm.TravelMode.DRIVING)) }
                        )
                        TravelModeButton(
                            icon = Icons.Default.DirectionsWalk,
                            label = "Walk",
                            selected = state.travelMode == Alarm.TravelMode.WALKING,
                            onClick = { onStateChange(state.copy(travelMode = Alarm.TravelMode.WALKING)) }
                        )
                        TravelModeButton(
                            icon = Icons.Default.DirectionsBike,
                            label = "Bike",
                            selected = state.travelMode == Alarm.TravelMode.BICYCLING,
                            onClick = { onStateChange(state.copy(travelMode = Alarm.TravelMode.BICYCLING)) }
                        )
                        TravelModeButton(
                            icon = Icons.Default.DirectionsBus,
                            label = "Transit",
                            selected = state.travelMode == Alarm.TravelMode.TRANSIT,
                            onClick = { onStateChange(state.copy(travelMode = Alarm.TravelMode.TRANSIT)) }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Buffer Time
                    OutlinedTextField(
                        value = state.bufferMinutes.toString(),
                        onValueChange = {
                            val minutes = it.toIntOrNull() ?: 0
                            if (minutes >= 0) {
                                onStateChange(state.copy(bufferMinutes = minutes))
                            }
                        },
                        label = { Text("Buffer Time (minutes)") },
                        leadingIcon = {
                            Icon(Icons.Default.Timer, contentDescription = null)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CANCEL")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onConfirm) {
                        Text(if (state.isNew) "ADD" else "SAVE")
                    }
                }
            }
        }
    }
}

@Composable
private fun TravelModeButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = if (selected) {
        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    } else {
        ButtonDefaults.buttonColors()
    }
    
    Button(
        onClick = onClick,
        colors = colors,
        modifier = Modifier.padding(4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun TimePickerDialog(
    initialTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTime by remember { mutableStateOf(initialTime) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onTimeSelected(selectedTime) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        },
        title = { Text("Set Time") },
        text = {
            // In a real app, you would use a proper TimePicker here
            // This is a simplified version for demonstration
            Column {
                // Simplified time picker UI
                // In a real app, use a proper time picker component
                Text(
                    text = "${selectedTime.hour}:${selectedTime.minute}",
                    style = MaterialTheme.typography.headlineMedium
                )
                // Add proper time picker controls here
            }
        }
    )
}
