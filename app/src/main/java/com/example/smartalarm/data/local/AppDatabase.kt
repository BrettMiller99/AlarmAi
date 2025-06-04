package com.example.smartalarm.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.smartalarm.data.local.dao.AlarmDao
import com.example.smartalarm.data.local.converter.Converters
import com.example.smartalarm.domain.model.Alarm

/**
 * The Room database for this app.
 */
@Database(
    entities = [Alarm::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * Gets the singleton instance of the database.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_alarm_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                
                INSTANCE = instance
                instance
            }
        }
    }
}
