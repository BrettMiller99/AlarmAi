package com.example.smartalarm.data.repository

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class LocationRepository @Inject constructor(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
) {
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    suspend fun getCurrentLocation(): Location {
        return getLastKnownLocation() ?: fetchCurrentLocation()
    }

    private suspend fun getLastKnownLocation(): Location? {
        return try {
            val locationResult = fusedLocationClient.lastLocation.await()
            locationResult.takeIf { it != null }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun fetchCurrentLocation(): Location = suspendCancellableCoroutine { continuation ->
        val callback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    continuation.resume(location)
                    fusedLocationClient.removeLocationUpdates(this)
                }
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                if (!availability.isLocationAvailable) {
                    continuation.resumeWithException(Exception("Location not available"))
                    fusedLocationClient.removeLocationUpdates(this)
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                callback,
                Looper.getMainLooper()
            )

            continuation.invokeOnCancellation {
                fusedLocationClient.removeLocationUpdates(callback)
            }
        } catch (e: SecurityException) {
            continuation.resumeWithException(e)
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }

    fun hasLocationPermission(): Boolean {
        return context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == 
               android.content.pm.PackageManager.PERMISSION_GRANTED ||
               context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == 
               android.content.pm.PackageManager.PERMISSION_GRANTED
    }
}
