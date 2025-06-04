package com.example.smartalarm.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.example.smartalarm.domain.model.Alarm
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A utility class for managing shared preferences.
 */
@Singleton
class PreferenceUtils @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    
    companion object {
        // Keys for shared preferences
        private const val KEY_FIRST_RUN = "first_run"
        private const val KEY_ALARM_COUNTER = "alarm_counter"
        private const val KEY_USE_24_HOUR_FORMAT = "use_24_hour_format"
        private const val KEY_USE_METRIC_SYSTEM = "use_metric_system"
        private const val KEY_DEFAULT_ALARM_VOLUME = "default_alarm_volume"
        private const val KEY_DEFAULT_ALARM_SNOOZE_DURATION = "default_alarm_snooze_duration"
        private const val KEY_DEFAULT_ALARM_RINGTONE = "default_alarm_ringtone"
        private const val KEY_DEFAULT_ALARM_VIBRATE = "default_alarm_vibrate"
        
        // Default values
        private const val DEFAULT_SNOOZE_DURATION = 10L // 10 minutes
        private const val DEFAULT_ALARM_VOLUME = 80 // 80% volume
        private const val DEFAULT_ALARM_VIBRATE = true
    }
    
    /**
     * Checks if this is the first run of the app.
     */
    var isFirstRun: Boolean
        get() = prefs.getBoolean(KEY_FIRST_RUN, true)
        set(value) = prefs.edit { putBoolean(KEY_FIRST_RUN, value) }
    
    /**
     * Gets the next available alarm ID and increments the counter.
     */
    fun getNextAlarmId(): Long {
        val nextId = prefs.getLong(KEY_ALARM_COUNTER, 0) + 1
        prefs.edit { putLong(KEY_ALARM_COUNTER, nextId) }
        return nextId
    }
    
    /**
     * Whether to use 24-hour format for time display.
     */
    var use24HourFormat: Boolean
        get() = prefs.getBoolean(KEY_USE_24_HOUR_FORMAT, false)
        set(value) = prefs.edit { putBoolean(KEY_USE_24_HOUR_FORMAT, value) }
    
    /**
     * Whether to use metric system for distance display.
     */
    var useMetricSystem: Boolean
        get() = prefs.getBoolean(KEY_USE_METRIC_SYSTEM, true)
        set(value) = prefs.edit { putBoolean(KEY_USE_METRIC_SYSTEM, value) }
    
    /**
     * The default alarm volume (0-100).
     */
    var defaultAlarmVolume: Int
        get() = prefs.getInt(KEY_DEFAULT_ALARM_VOLUME, DEFAULT_ALARM_VOLUME)
        set(value) = prefs.edit { putInt(KEY_DEFAULT_ALARM_VOLUME, value.coerceIn(0, 100)) }
    
    /**
     * The default snooze duration in minutes.
     */
    var defaultSnoozeDuration: Long
        get() = prefs.getLong(KEY_DEFAULT_ALARM_SNOOZE_DURATION, DEFAULT_SNOOZE_DURATION)
        set(value) = prefs.edit { putLong(KEY_DEFAULT_ALARM_SNOOZE_DURATION, value.coerceAtLeast(1)) }
    
    /**
     * The default alarm ringtone URI.
     */
    var defaultAlarmRingtone: String?
        get() = prefs.getString(KEY_DEFAULT_ALARM_RINGTONE, null)
        set(value) = prefs.edit { putString(KEY_DEFAULT_ALARM_RINGTONE, value) }
    
    /**
     * Whether to vibrate by default for alarms.
     */
    var defaultAlarmVibrate: Boolean
        get() = prefs.getBoolean(KEY_DEFAULT_ALARM_VIBRATE, DEFAULT_ALARM_VIBRATE)
        set(value) = prefs.edit { putBoolean(KEY_DEFAULT_ALARM_VIBRATE, value) }
    
    /**
     * Gets a string preference value.
     */
    fun getString(key: String, defaultValue: String? = null): String? {
        return prefs.getString(key, defaultValue)
    }
    
    /**
     * Gets an integer preference value.
     */
    fun getInt(key: String, defaultValue: Int): Int {
        return prefs.getInt(key, defaultValue)
    }
    
    /**
     * Gets a long preference value.
     */
    fun getLong(key: String, defaultValue: Long): Long {
        return prefs.getLong(key, defaultValue)
    }
    
    /**
     * Gets a boolean preference value.
     */
    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }
    
    /**
     * Sets a string preference value.
     */
    fun setString(key: String, value: String?) {
        prefs.edit { putString(key, value) }
    }
    
    /**
     * Sets an integer preference value.
     */
    fun setInt(key: String, value: Int) {
        prefs.edit { putInt(key, value) }
    }
    
    /**
     * Sets a long preference value.
     */
    fun setLong(key: String, value: Long) {
        prefs.edit { putLong(key, value) }
    }
    
    /**
     * Sets a boolean preference value.
     */
    fun setBoolean(key: String, value: Boolean) {
        prefs.edit { putBoolean(key, value) }
    }
    
    /**
     * Clears all preferences.
     */
    fun clear() {
        prefs.edit { clear() }
    }
}
