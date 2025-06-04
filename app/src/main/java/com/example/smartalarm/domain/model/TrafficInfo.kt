package com.example.smartalarm.domain.model

import java.time.Duration
import java.time.LocalDateTime

/**
 * Represents traffic information for a route.
 *
 * @property durationInTraffic The estimated duration of the trip in traffic, in seconds
 * @property durationWithoutTraffic The estimated duration of the trip without traffic, in seconds
 * @property distanceMeters The distance of the route in meters
 * @property departureTime The time of departure used for the calculation
 * @property arrivalTime The estimated time of arrival
 * @property hasTrafficData Whether traffic data was available for the calculation
 * @property trafficDelaySeconds The delay caused by traffic, in seconds (0 if no traffic data)
 */
data class TrafficInfo(
    val durationInTraffic: Long,
    val durationWithoutTraffic: Long,
    val distanceMeters: Int,
    val departureTime: LocalDateTime,
    val arrivalTime: LocalDateTime,
    val hasTrafficData: Boolean,
    val trafficDelaySeconds: Long = maxOf(0, durationInTraffic - durationWithoutTraffic)
) {
    /**
     * The duration of the trip as a [Duration] object.
     */
    val duration: Duration
        get() = Duration.ofSeconds(durationInTraffic)
    
    /**
     * The traffic delay as a [Duration] object.
     */
    val trafficDelay: Duration
        get() = Duration.ofSeconds(trafficDelaySeconds)
    
    /**
     * The distance of the trip in kilometers.
     */
    val distanceKm: Double
        get() = distanceMeters / 1000.0
    
    /**
     * The distance of the trip in miles.
     */
    val distanceMiles: Double
        get() = distanceMeters / 1609.34
    
    companion object {
        /**
         * Creates a [TrafficInfo] with the given duration and current time.
         * This is useful for testing or when traffic data is not available.
         */
        fun createWithoutTraffic(
            durationSeconds: Long,
            distanceMeters: Int,
            departureTime: LocalDateTime = LocalDateTime.now()
        ): TrafficInfo {
            return TrafficInfo(
                durationInTraffic = durationSeconds,
                durationWithoutTraffic = durationSeconds,
                distanceMeters = distanceMeters,
                departureTime = departureTime,
                arrivalTime = departureTime.plusSeconds(durationSeconds),
                hasTrafficData = false
            )
        }
    }
}
