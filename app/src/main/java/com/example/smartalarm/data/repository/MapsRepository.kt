package com.example.smartalarm.data.repository

import com.example.smartalarm.data.remote.api.GoogleMapsApi
import com.example.smartalarm.data.remote.response.DistanceMatrixResponse
import com.example.smartalarm.data.remote.response.GeocodingResponse
import com.example.smartalarm.domain.model.Alarm
import com.example.smartalarm.util.Result
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapsRepository @Inject constructor(
    private val api: GoogleMapsApi,
    private val ioDispatcher: CoroutineDispatcher,
    private val apiKey: String
) {
    
    suspend fun getTravelTime(
        origin: LatLng,
        destination: LatLng,
        travelMode: Alarm.TravelMode,
        departureTime: Date? = null
    ): Result<Int> = withContext(ioDispatcher) {
        try {
            val originStr = "${origin.latitude},${origin.longitude}"
            val destStr = "${destination.latitude},${destination.longitude}"
            val mode = when (travelMode) {
                Alarm.TravelMode.DRIVING -> "driving"
                Alarm.TravelMode.WALKING -> "walking"
                Alarm.TravelMode.BICYCLING -> "bicycling"
                Alarm.TravelMode.TRANSIT -> "transit"
                else -> "driving"
            }
            
            val departureTimeStr = departureTime?.let { "now" } ?: ""
            
            val response = api.getDistanceMatrix(
                origins = originStr,
                destinations = destStr,
                mode = mode,
                departureTime = departureTimeStr,
                apiKey = apiKey
            )
            
            if (response.status == "OK" && 
                response.rows.isNotEmpty() && 
                response.rows[0].elements.isNotEmpty()) {
                
                val element = response.rows[0].elements[0]
                if (element.status == "OK") {
                    // Return duration in minutes
                    val durationInSeconds = element.durationInTraffic?.value ?: element.duration?.value
                    if (durationInSeconds != null) {
                        Result.Success((durationInSeconds / 60).toInt())
                    } else {
                        Result.Error(Exception("No duration information available"))
                    }
                } else {
                    Result.Error(Exception("No route found: ${element.status}"))
                }
            } else {
                Result.Error(Exception("Invalid response: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    suspend fun geocodeAddress(address: String): Result<LatLng> = withContext(ioDispatcher) {
        try {
            val response = api.geocodeAddress(address, apiKey)
            if (response.status == "OK" && response.results.isNotEmpty()) {
                val location = response.results[0].geometry.location
                Result.Success(LatLng(location.lat, location.lng))
            } else {
                Result.Error(Exception("Geocoding failed: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    suspend fun reverseGeocode(latLng: LatLng): Result<String> = withContext(ioDispatcher) {
        try {
            val response = api.reverseGeocode("${latLng.latitude},${latLng.longitude}", apiKey)
            if (response.status == "OK" && response.results.isNotEmpty()) {
                Result.Success(response.results[0].formattedAddress)
            } else {
                Result.Error(Exception("Reverse geocoding failed: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
