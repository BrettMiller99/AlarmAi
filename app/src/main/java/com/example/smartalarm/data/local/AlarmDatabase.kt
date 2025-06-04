package com.example.smartalarm.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.smartalarm.data.local.converter.Converters
import com.example.smartalarm.data.local.dao.AlarmDao
import com.example.smartalarm.data.local.entity.AlarmEntity
import com.example.smartalarm.util.Constants

/**
 * Room database class for the Smart Alarm application.
 * Manages the database creation and version management.
 */
@Database(
    entities = [AlarmEntity::class],
    version = Constants.DATABASE_VERSION,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AlarmDatabase : RoomDatabase() {
    
    /**
     * Provides access to the [AlarmDao] methods for database operations.
     */
    abstract fun alarmDao(): AlarmDao
    
    companion object {
        @Volatile
        private var INSTANCE: AlarmDatabase? = null
        
        /**
         * Gets the singleton instance of [AlarmDatabase].
         * 
         * @param context The application context.
         * @return The singleton instance of [AlarmDatabase].
         */
        fun getDatabase(context: Context): AlarmDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AlarmDatabase::class.java,
                    Constants.DATABASE_NAME
                )
                .addCallback(object : RoomDatabase.Callback() {
                    // You can add database callbacks here if needed
                    // For example, to populate the database when it's first created
                })
                .fallbackToDestructiveMigration() // Wipes and rebuilds instead of migrating if no Migration object
                .build()
                
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Destroys the current database instance.
         * Used for testing purposes.
         */
        @Synchronized
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
