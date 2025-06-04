package com.example.smartalarm.data.remote.api

import com.example.smartalarm.data.remote.dto.DistanceMatrixResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface for Google Maps API.
 */
interface GoogleMapsApiService {
    
    /**
     * Gets travel distance and duration for a route.
     * 
     * @param origins Origin address or lat,lng
     * @param destinations Destination address or lat,lng
     * @param key API key
     * @param mode Travel mode (driving, walking, bicycling, transit)
     * @param departureTime Desired time of departure in seconds since midnight, January 1, 1970 UTC.
     * @param trafficModel Specifies the assumptions to use when calculating time in traffic.
     * @param units Unit system to use (metric or imperial)
     * @return [DistanceMatrixResponse] containing travel information
     */
    @GET("distancematrix/json")
    suspend fun getDistanceMatrix(
        @Query("origins") origins: String,
        @Query("destinations") destinations: String,
        @Query("key") key: String,
        @Query("mode") mode: String = "driving",
        @Query("departure_time") departureTime: Long? = null,
        @Query("traffic_model") trafficModel: String = "best_guess",
        @Query("units") units: String = "imperial"
    ): DistanceMatrixResponse
}
