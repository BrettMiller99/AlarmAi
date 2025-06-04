package com.example.smartalarm.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Utility class for handling location-related operations.
 * This includes checking permissions, getting the current location, and calculating distances.
 */
@Singleton
class LocationUtils @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    companion object {
        // Maximum age for cached location in milliseconds (5 minutes)
        private const val MAX_LOCATION_AGE_MS = TimeUnit.MINUTES.toMillis(5)
        
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
    }

    /**
     * Check if the app has the required location permissions.
     * @return true if all required permissions are granted, false otherwise
     */
    fun hasLocationPermission(): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Check if location services are enabled on the device.
     * @return true if location services are enabled, false otherwise
     */
    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || 
               locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /**
     * Get the last known location of the device.
     * @return The last known location, or null if not available
     */
    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): Location? {
        if (!hasLocationPermission()) {
            return null
        }
        
        return try {
            // First try to get the last known location (quick but might be outdated)
            val lastLocation = fusedLocationClient.lastLocation.await()
            
            // If last location is not available or too old, request a fresh location
            if (lastLocation == null || System.currentTimeMillis() - lastLocation.time > MAX_LOCATION_AGE_MS) {
                getCurrentLocation()
            } else {
                lastLocation
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get the current location of the device.
     * @return The current location, or null if not available
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) {
            return null
        }

        return try {
            val cancellationTokenSource = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).await()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Calculate the distance between two locations in meters.
     * @param startLatLng The starting location
     * @param endLatLng The destination location
     * @return The distance in meters
     */
    fun calculateDistance(
        startLatLng: LatLng,
        endLatLng: LatLng
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            startLatLng.latitude,
            startLatLng.longitude,
            endLatLng.latitude,
            endLatLng.longitude,
            results
        )
        return results[0]
    }

    /**
     * Calculate the approximate travel time in minutes between two locations.
     * @param startLatLng The starting location
     * @param endLatLng The destination location
     * @param travelMode The mode of transportation (driving, walking, bicycling, transit)
     * @return The estimated travel time in minutes, or null if calculation fails
     */
    suspend fun calculateTravelTime(
        startLatLng: LatLng,
        endLatLng: LatLng,
        travelMode: String = "driving"
    ): Int? {
        // This is a simplified implementation. In a real app, you would use the Google Maps Directions API
        // to get accurate travel times based on current traffic conditions.
        
        // Calculate the straight-line distance in kilometers
        val distanceKm = calculateDistance(startLatLng, endLatLng) / 1000f
        
        // Estimate speed based on travel mode (km/h)
        val averageSpeedKmH = when (travelMode.lowercase()) {
            "walking" -> 5f
            "bicycling" -> 15f
            "transit" -> 25f
            else -> 50f // driving
        }
        
        // Calculate time in hours, then convert to minutes
        val timeHours = distanceKm / averageSpeedKmH
        return (timeHours * 60).toInt().coerceAtLeast(1) // At least 1 minute
    }

    /**
     * Get the bearing between two points in degrees.
     * @param startLatLng The starting location
     * @param endLatLng The destination location
     * @return The bearing in degrees (0-360)
     */
    fun getBearing(startLatLng: LatLng, endLatLng: LatLng): Float {
        val startLat = Math.toRadians(startLatLng.latitude)
        val startLng = Math.toRadians(startLatLng.longitude)
        val endLat = Math.toRadians(endLatLng.latitude)
        val endLng = Math.toRadians(endLatLng.longitude)

        val dLng = endLng - startLng
        val y = sin(dLng) * cos(endLat)
        val x = cos(startLat) * sin(endLat) - sin(startLat) * cos(endLat) * cos(dLng)
        
        var bearing = Math.toDegrees(atan2(y, x)).toFloat()
        return (bearing + 360) % 360 // Normalize to 0-360
    }

    /**
     * Formats a distance in meters to a human-readable string.
     * @param distanceInMeters The distance in meters
     * @param useMetricSystem Whether to use metric system (true) or imperial (false)
     * @return A formatted string representing the distance (e.g., "1.2 km" or "0.7 mi")
     */
    fun formatDistance(distanceInMeters: Float, useMetricSystem: Boolean = true): String {
        return if (useMetricSystem) {
            if (distanceInMeters < 1000) {
                "${distanceInMeters.toInt()} m"
            } else {
                "${String.format("%.1f", distanceInMeters / 1000)} km"
            }
        } else {
            val distanceInFeet = distanceInMeters * 3.28084f
            if (distanceInFeet < 1000) {
                "${distanceInFeet.toInt()} ft"
            } else {
                "${String.format("%.1f", distanceInFeet / 5280)} mi"
            }
        }
    }

    /**
     * Formats a duration in minutes to a human-readable string.
     * @param minutes The duration in minutes
     * @return A formatted string (e.g., "1h 30m" or "45m")
     */
    fun formatDuration(minutes: Int): String {
        return if (minutes >= 60) {
            val hours = minutes / 60
            val remainingMinutes = minutes % 60
            if (remainingMinutes > 0) "${hours}h ${remainingMinutes}m" else "${hours}h"
        } else {
            "${minutes}m"
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
    
    /**
     * Creates a LocationRequest with default settings.
     */
    fun createLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(10)
            fastestInterval = TimeUnit.SECONDS.toMillis(5)
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
    }
}
