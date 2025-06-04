package com.example.smartalarm.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.tasks.await

/**
 * Utility class for location-related operations.
 */
object LocationUtils {
    
    // Location permission request code
    const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    
    // Required permissions for location access
    val REQUIRED_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
    
    /**
     * Checks if the app has the required location permissions.
     */
    fun hasLocationPermissions(context: Context): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    /**
     * Gets the last known location of the device.
     * @return A [Task] that resolves to the last known [Location] or null if not available
     */
    fun getLastKnownLocation(context: Context): Task<Location> {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        
        // Check for location permissions
        if (!hasLocationPermissions(context)) {
            throw SecurityException("Location permissions not granted")
        }
        
        return fusedLocationClient.lastLocation
    }
    
    /**
     * Gets the last known location of the device as a [LatLng].
     * @return A [LatLng] representing the last known location, or null if not available
     */
    suspend fun getLastKnownLatLng(context: Context): LatLng? {
        return try {
            val location = getLastKnownLocation(context).await()
            location?.let { LatLng(it.latitude, it.longitude) }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Calculates the distance between two points in meters.
     */
    fun calculateDistance(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(startLat, startLng, endLat, endLng, results)
        return results[0]
    }
    
    /**
     * Calculates the distance between two [LatLng] points in meters.
     */
    fun calculateDistance(start: LatLng, end: LatLng): Float {
        return calculateDistance(start.latitude, start.longitude, end.latitude, end.longitude)
    }
    
    /**
     * Formats a distance in meters to a human-readable string.
     * @param distanceInMeters The distance in meters
     * @param useMetricSystem Whether to use metric system (true) or imperial (false)
     */
    fun formatDistance(distanceInMeters: Float, useMetricSystem: Boolean = true): String {
        return if (useMetricSystem) {
            when {
                distanceInMeters < 1000 -> "${distanceInMeters.toInt()} m"
                else -> String.format("%.1f km", distanceInMeters / 1000f)
            }
        } else {
            val distanceInFeet = distanceInMeters * 3.28084f
            when {
                distanceInFeet < 1000 -> "${distanceInFeet.toInt()} ft"
                else -> {
                    val distanceInMiles = distanceInMeters * 0.000621371f
                    String.format("%.1f mi", distanceInMiles)
                }
            }
        }
    }
    
    /**
     * Checks if a location is valid.
     */
    fun isValidLocation(lat: Double, lng: Double): Boolean {
        return lat in -90.0..90.0 && lng in -180.0..180.0
    }
    
    /**
     * Checks if a location is valid.
     */
    fun isValidLocation(latLng: LatLng): Boolean {
        return isValidLocation(latLng.latitude, latLng.longitude)
    }
}
