package com.example.smartalarm.di

import android.content.Context
import com.example.smartalarm.data.local.AppDatabase
import com.example.smartalarm.data.local.dao.AlarmDao
import com.example.smartalarm.data.repository.AlarmRepository
import com.example.smartalarm.data.repository.AlarmRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module that provides data layer dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
    
    @Provides
    fun provideAlarmDao(database: AppDatabase): AlarmDao {
        return database.alarmDao()
    }
    
    @Provides
    fun provideAlarmRepository(alarmDao: AlarmDao): AlarmRepository {
        return AlarmRepositoryImpl(alarmDao)
    }
}
