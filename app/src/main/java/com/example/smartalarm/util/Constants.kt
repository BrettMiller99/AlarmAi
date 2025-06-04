package com.example.smartalarm.util

import com.example.smartalarm.R

object Constants {
    // Google Maps API
    const val GOOGLE_MAPS_BASE_URL = "https://maps.googleapis.com/maps/api/"
    
    // Shared Preferences
    const val PREF_NAME = "smart_alarm_prefs"
    const val PREF_FIRST_LAUNCH = "pref_first_launch"
    const val PREF_DEFAULT_ALARM_VOLUME = "pref_default_alarm_volume"
    const val PREF_DEFAULT_ALARM_SOUND = "pref_default_alarm_sound"
    const val PREF_DEFAULT_VIBRATION_PATTERN = "pref_default_vibration_pattern"
    
    // Notification Channels
    const val NOTIFICATION_CHANNEL_ALARMS = "alarm_channel"
    const val NOTIFICATION_CHANNEL_SERVICE = "service_channel"
    
    // Notification IDs
    const val NOTIFICATION_ID_ALARM = 1000
    const val NOTIFICATION_ID_SERVICE = 1001
    
    // Actions
    const val ACTION_ALARM_TRIGGER = "com.example.smartalarm.ACTION_ALARM_TRIGGER"
    const val ACTION_SNOOZE = "com.example.smartalarm.ACTION_SNOOZE"
    const val ACTION_DISMISS = "com.example.smartalarm.ACTION_DISMISS"
    const val ACTION_STOP_ALARM = "com.example.smartalarm.ACTION_STOP_ALARM"
    const val ACTION_SCHEDULE_ALARM = "com.example.smartalarm.ACTION_SCHEDULE_ALARM"
    const val ACTION_CANCEL_ALARM = "com.example.smartalarm.ACTION_CANCEL_ALARM"
    const val ACTION_RESCHEDULE_ALL_ALARMS = "com.example.smartalarm.ACTION_RESCHEDULE_ALL_ALARMS"
    const val ACTION_ALARM_DISMISSED = "com.example.smartalarm.ACTION_ALARM_DISMISSED"
    
    // Extras
    const val EXTRA_ALARM_ID = "extra_alarm_id"
    const val EXTRA_ALARM = "extra_alarm"
    const val EXTRA_ALARM_TIME = "extra_alarm_time"
    const val EXTRA_ALARM_LABEL = "extra_alarm_label"
    const val EXTRA_ALARM_SOUND = "extra_alarm_sound"
    const val EXTRA_ALARM_VOLUME = "extra_alarm_volume"
    const val EXTRA_ALARM_VIBRATION = "extra_alarm_vibration"
    const val EXTRA_ALARM_SNOOZE_DURATION = "extra_alarm_snooze_duration"
    
    // Request Codes
    const val REQUEST_CODE_ALARM = 1001
    const val REQUEST_CODE_SNOOZE = 1002
    const val REQUEST_CODE_DISMISS = 1003
    const val REQUEST_CODE_LOCATION_PERMISSION = 1004
    const val REQUEST_CODE_ALARM_PERMISSION = 1005
    const val REQUEST_CODE_NOTIFICATION_PERMISSION = 1006
    const val REQUEST_CODE_EXACT_ALARM_PERMISSION = 1007
    
    // Alarm Types
    const val ALARM_TYPE_ONE_TIME = 0
    const val ALARM_TYPE_REPEATING = 1
    const val ALARM_TYPE_SMART = 2
    
    // Alarm States
    const val ALARM_STATE_DISABLED = 0
    const val ALARM_STATE_ENABLED = 1
    const val ALARM_STATE_SNOOZED = 2
    const val ALARM_STATE_FIRED = 3
    
    // Alarm Actions
    const val ALARM_ACTION_ENABLE = "enable"
    const val ALARM_ACTION_DISABLE = "disable"
    const val ALARM_ACTION_DELETE = "delete"
    const val ALARM_ACTION_SNOOZE = "snooze"
    const val ALARM_ACTION_DISMISS = "dismiss"
    
    // Alarm Modes
    const val ALARM_MODE_NORMAL = "normal"
    const val ALARM_MODE_GRADUAL = "gradual"
    const val ALARM_MODE_SMART = "smart"
    
    // Travel Modes
    const val TRAVEL_MODE_DRIVING = "driving"
    const val TRAVEL_MODE_WALKING = "walking"
    const val TRAVEL_MODE_BICYCLING = "bicycling"
    const val TRAVEL_MODE_TRANSIT = "transit"
    
    // Intent Actions for Alarm Scheduling
    const val ACTION_SCHEDULE_ALARM = "com.example.smartalarm.ACTION_SCHEDULE_ALARM"
    const val ACTION_CANCEL_ALARM = "com.example.smartalarm.ACTION_CANCEL_ALARM"
    const val ACTION_RESCHEDULE_ALL_ALARMS = "com.example.smartalarm.ACTION_RESCHEDULE_ALL_ALARMS"
    const val ACTION_ALARM_DISMISSED = "com.example.smartalarm.ACTION_ALARM_DISMISSED"
    const val ACTION_ALARM_SNOOZE = "com.example.smartalarm.ACTION_ALARM_SNOOZE"
    const val ACTION_ALARM_STOPPED = "com.example.smartalarm.ACTION_ALARM_STOPPED"
    
    // Intent Extras for Alarm Scheduling
    const val EXTRA_ALARM_ID = "extra_alarm_id"
    const val EXTRA_ALARM = "extra_alarm"
    const val EXTRA_ALARM_TIME = "extra_alarm_time"
    const val EXTRA_ALARM_LABEL = "extra_alarm_label"
    const val EXTRA_ALARM_SOUND = "extra_alarm_sound"
    const val EXTRA_ALARM_VOLUME = "extra_alarm_volume"
    const val EXTRA_ALARM_VIBRATION = "extra_alarm_vibration"
    const val EXTRA_ALARM_SNOOZE_DURATION = "extra_alarm_snooze_duration"
    const val EXTRA_ALARM_TYPE = "extra_alarm_type"
    const val EXTRA_ALARM_DAYS = "extra_alarm_days"
    const val EXTRA_ALARM_DESTINATION = "extra_alarm_destination"
    const val EXTRA_ALARM_TRAVEL_MODE = "extra_alarm_travel_mode"
    const val EXTRA_ALARM_BUFFER_MINUTES = "extra_alarm_buffer_minutes"
    const val EXTRA_ALARM_IS_SMART = "extra_alarm_is_smart"
    
    // Request Codes
    const val REQUEST_CODE_ALARM_PERMISSIONS = 2001
    const val REQUEST_CODE_ALARM_SETTINGS = 2002
    
    // Default Values
    const val DEFAULT_SNOOZE_DURATION_MINUTES = 10L
    const val DEFAULT_ALARM_VOLUME = 80 // 0-100
    const val DEFAULT_VIBRATION_PATTERN = "0,1000,1000,1000,1000"
    
    // Timeouts
    const val LOCATION_TIMEOUT_MILLIS = 30000L // 30 seconds
    const val NETWORK_TIMEOUT_MILLIS = 10000L // 10 seconds
    
    // Animation Durations
    const val ANIM_DURATION_SHORT = 200L
    const val ANIM_DURATION_MEDIUM = 300L
    const val ANIM_DURATION_LONG = 500L
    
    // Bundle Keys
    const val BUNDLE_ALARM = "bundle_alarm"
    const val BUNDLE_IS_NEW_ALARM = "bundle_is_new_alarm"
    
    // Result Codes
    const val RESULT_ALARM_ADDED = 1
    const val RESULT_ALARM_UPDATED = 2
    const val RESULT_ALARM_DELETED = 3
    
    // Intent Actions
    const val INTENT_ACTION_ALARM_UPDATED = "com.example.smartalarm.ALARM_UPDATED"
    const val INTENT_ACTION_ALARM_DELETED = "com.example.smartalarm.ALARM_DELETED"
    
    // Shared Prefs Keys
    const val PREF_KEY_ALARM_SOUND = "pref_alarm_sound"
    const val PREF_KEY_ALARM_VOLUME = "pref_alarm_volume"
    const val PREF_KEY_VIBRATION_PATTERN = "pref_vibration_pattern"
    const val PREF_KEY_SNOOZE_DURATION = "pref_snooze_duration"
    const val PREF_KEY_IS_24_HOUR_FORMAT = "pref_is_24_hour_format"
    
    // Request Permissions
    const val PERMISSION_REQUEST_CODE = 1000
    
    // Alarm Types
    const val ALARM_TYPE_REGULAR = 0
    const val ALARM_TYPE_SMART = 1
    
    // Smart Alarm Settings
    const val DEFAULT_TRAVEL_MODE = "driving"
    const val DEFAULT_BUFFER_MINUTES = 15
    const val MAX_BUFFER_MINUTES = 120
    const val MIN_BUFFER_MINUTES = 5
    
    // Error Messages
    const val ERROR_LOCATION_PERMISSION_DENIED = "Location permission denied"
    const val ERROR_LOCATION_UNAVAILABLE = "Location not available"
    const val ERROR_NETWORK_UNAVAILABLE = "Network not available"
    const val ERROR_INVALID_DESTINATION = "Invalid destination address"
    const val ERROR_CALCULATING_ROUTE = "Error calculating route"
    
    // Log Tags
    const val TAG_ALARM_RECEIVER = "AlarmReceiver"
    const val TAG_ALARM_SERVICE = "AlarmService"
    const val TAG_ALARM_NOTIFICATION_SERVICE = "AlarmNotificationService"
    const val TAG_ALARM_SCHEDULER = "AlarmScheduler"
    const val TAG_BOOT_RECEIVER = "BootCompletedReceiver"
    
    // Default Values for New Alarms
    val DEFAULT_ALARM_NAME = "Alarm"
    val DEFAULT_ALARM_TIME = java.time.LocalTime.of(8, 0) // 8:00 AM
    val DEFAULT_ALARM_DAYS = emptySet<Int>()
    const val DEFAULT_ALARM_IS_ENABLED = true
    const val DEFAULT_ALARM_IS_VIBRATE = true
    const val DEFAULT_ALARM_IS_SMART = false
    const val DEFAULT_ALARM_DESTINATION = ""
    const val DEFAULT_ALARM_TRAVEL_MODE = "driving"
    const val DEFAULT_ALARM_BUFFER_MINUTES = 15
    
    // Request Timeouts
    const val REQUEST_TIMEOUT_MILLIS = 30000L // 30 seconds
    
    // Animation Resources
    val ALARM_ANIMATION = R.raw.alarm_animation
    
    // Sound Resources
    val DEFAULT_ALARM_SOUND = R.raw.alarm_sound
    
    // Icon Resources
    val ALARM_ICON = R.drawable.ic_alarm
    val SNOOZE_ICON = R.drawable.ic_snooze
    val DISMISS_ICON = R.drawable.ic_dismiss
    
    // String Resources
    val ALARM_NOTIFICATION_TITLE = R.string.alarm_notification_title
    val ALARM_NOTIFICATION_TEXT = R.string.alarm_notification_text
    val SNOOZE_BUTTON = R.string.snooze_button
    val DISMISS_BUTTON = R.string.dismiss_button
    
    // Colors
    val COLOR_ALARM_ACTIVE = R.color.colorAlarmActive
    val COLOR_ALARM_INACTIVE = R.color.colorAlarmInactive
    
    // Dimen
    val ALARM_ITEM_HEIGHT = R.dimen.alarm_item_height
    val ALARM_ITEM_PADDING = R.dimen.alarm_item_padding
}
