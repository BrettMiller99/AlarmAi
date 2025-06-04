package com.example.smartalarm.di

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import androidx.room.Room
import com.example.smartalarm.BuildConfig
import com.example.smartalarm.data.local.AppDatabase
import com.example.smartalarm.data.remote.api.GoogleMapsApi
import com.example.smartalarm.data.repository.AlarmRepository
import com.example.smartalarm.data.repository.AlarmRepositoryImpl
import com.example.smartalarm.data.repository.LocationRepository
import com.example.smartalarm.data.repository.LocationRepositoryImpl
import com.example.smartalarm.data.repository.MapsRepository
import com.example.smartalarm.data.repository.MapsRepositoryImpl
import com.example.smartalarm.domain.repository.AlarmScheduler
import com.example.smartalarm.util.AlarmSchedulerImpl
import com.example.smartalarm.util.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .create()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            httpClient.addInterceptor(logging)
        }

        return httpClient.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.GOOGLE_MAPS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideGoogleMapsApi(retrofit: Retrofit): GoogleMapsApi {
        return retrofit.create(GoogleMapsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Provides
    @Singleton
    fun provideAudioManager(
        @ApplicationContext context: Context
    ): AudioManager {
        return context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideAlarmScheduler(
        @ApplicationContext context: Context,
        alarmRepository: AlarmRepository
    ): AlarmScheduler {
        return AlarmSchedulerImpl(context, alarmRepository)
    }

    @Provides
    @Singleton
    fun provideAlarmRepository(
        database: AppDatabase,
        mapsRepository: MapsRepository,
        locationRepository: LocationRepository
    ): AlarmRepository {
        return AlarmRepositoryImpl(
            database.alarmDao(),
            mapsRepository,
            locationRepository
        )
    }

    @Provides
    @Singleton
    fun provideMapsRepository(
        api: GoogleMapsApi,
        @ApplicationContext context: Context
    ): MapsRepository {
        return MapsRepositoryImpl(
            api = api,
            ioDispatcher = kotlinx.coroutines.Dispatchers.IO,
            apiKey = BuildConfig.GOOGLE_MAPS_API_KEY
        )
    }

    @Provides
    @Singleton
    fun provideLocationRepository(
        @ApplicationContext context: Context,
        fusedLocationClient: FusedLocationProviderClient
    ): LocationRepository {
        return LocationRepositoryImpl(context, fusedLocationClient)
    }
}
