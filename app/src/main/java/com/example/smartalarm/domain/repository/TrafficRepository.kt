package com.example.smartalarm.domain.repository

import com.example.smartalarm.domain.model.TrafficInfo
import java.time.LocalDateTime

/**
 * Repository interface for traffic-related operations.
 */
interface TrafficRepository {
    /**
     * Gets the estimated travel time including traffic.
     * 
     * @param originLat Origin latitude
     * @param originLng Origin longitude
     * @param destinationLat Destination latitude
     * @param destinationLng Destination longitude
     * @param departureTime The time of departure (null for current time)
     * @return [TrafficInfo] containing travel time and other information
     */
    suspend fun getTrafficInfo(
        originLat: Double,
        originLng: Double,
        destinationLat: Double,
        destinationLng: Double,
        departureTime: LocalDateTime? = null
    ): Result<TrafficInfo>
    
    /**
     * Gets the estimated travel time including traffic using place names/addresses.
     * 
     * @param origin Origin address or place name
     * @param destination Destination address or place name
     * @param departureTime The time of departure (null for current time)
     * @return [TrafficInfo] containing travel time and other information
     */
    suspend fun getTrafficInfoByAddress(
        origin: String,
        destination: String,
        departureTime: LocalDateTime? = null
    ): Result<TrafficInfo>
}
