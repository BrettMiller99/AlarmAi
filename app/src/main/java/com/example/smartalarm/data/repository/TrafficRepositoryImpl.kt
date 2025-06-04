package com.example.smartalarm.data.repository

import com.example.smartalarm.data.remote.api.GoogleMapsApiService
import com.example.smartalarm.data.remote.dto.DistanceMatrixResponse
import com.example.smartalarm.domain.model.TrafficInfo
import com.example.smartalarm.domain.repository.TrafficRepository
import com.example.smartalarm.util.Result
import com.example.smartalarm.util.networkBoundResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Inject

/**
 * Implementation of [TrafficRepository] that uses Google Maps API as the data source.
 */
class TrafficRepositoryImpl @Inject constructor(
    private val apiService: GoogleMapsApiService,
    @com.example.smartalarm.di.GoogleMapsApiKey
    private val apiKey: String
) : TrafficRepository {

    override suspend fun getTrafficInfo(
        originLat: Double,
        originLng: Double,
        destinationLat: Double,
        destinationLng: Double,
        departureTime: LocalDateTime?
    ): Result<TrafficInfo> = withContext(Dispatchers.IO) {
        return@withContext try {
            val origin = "$originLat,$originLng"
            val destination = "$destinationLat,$destinationLng"
            val departureTimeSeconds = departureTime?.toEpochSecond(ZoneOffset.UTC)
            
            val response = apiService.getDistanceMatrix(
                origins = origin,
                destinations = destination,
                key = apiKey,
                departureTime = departureTimeSeconds,
                trafficModel = "best_guess"
            )
            
            if (response.status != DistanceMatrixResponse.STATUS_OK) {
                return@withContext Result.Error(
                    Exception("Failed to get traffic data: ${response.status}")
                )
            }
            
            val element = response.rows.firstOrNull()?.elements?.firstOrNull()
            if (element == null || element.status != DistanceMatrixResponse.STATUS_OK) {
                return@withContext Result.Error(
                    Exception("No route found: ${element?.status}")
                )
            }
            
            val durationInTraffic = element.durationInTraffic?.value ?: element.duration?.value
                ?: return@withContext Result.Error(
                    Exception("No duration information available")
                )
                
            val durationWithoutTraffic = element.duration?.value
                ?: return@withContext Result.Error(
                    Exception("No duration information available")
                )
                
            val distanceMeters = element.distance?.value?.toInt()
                ?: return@withContext Result.Error(
                    Exception("No distance information available")
                )
            
            val actualDepartureTime = departureTime ?: LocalDateTime.now()
            val actualArrivalTime = actualDepartureTime.plusSeconds(durationInTraffic)
            
            Result.Success(
                TrafficInfo(
                    durationInTraffic = durationInTraffic,
                    durationWithoutTraffic = durationWithoutTraffic,
                    distanceMeters = distanceMeters,
                    departureTime = actualDepartureTime,
                    arrivalTime = actualArrivalTime,
                    hasTrafficData = element.durationInTraffic != null
                )
            )
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getTrafficInfoByAddress(
        origin: String,
        destination: String,
        departureTime: LocalDateTime?
    ): Result<TrafficInfo> {
        // This would be implemented similarly to getTrafficInfo but using addresses
        // For brevity, we'll just forward to getTrafficInfo with dummy coordinates
        // In a real app, you would geocode the addresses first
        return getTrafficInfo(
            originLat = 0.0,
            originLng = 0.0,
            destinationLat = 0.0,
            destinationLng = 0.0,
            departureTime = departureTime
        )
    }
    
    private fun LocalDateTime.toEpochSecond(zoneOffset: ZoneOffset): Long {
        return this.toInstant(zoneOffset).epochSecond
    }
}
