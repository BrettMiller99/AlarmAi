package com.example.smartalarm.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Response DTO for Google Maps Distance Matrix API.
 */
data class DistanceMatrixResponse(
    @SerializedName("status")
    val status: String,
    
    @SerializedName("origin_addresses")
    val originAddresses: List<String>,
    
    @SerializedName("destination_addresses")
    val destinationAddresses: List<String>,
    
    @SerializedName("rows")
    val rows: List<Row>
) {
    data class Row(
        @SerializedName("elements")
        val elements: List<Element>
    )
    
    data class Element(
        @SerializedName("status")
        val status: String,
        
        @SerializedName("duration")
        val duration: Duration?,
        
        @SerializedName("duration_in_traffic")
        val durationInTraffic: Duration?,
        
        @SerializedName("distance")
        val distance: Distance?
    )
    
    data class Duration(
        @SerializedName("value")
        val value: Long,  // Duration in seconds
        
        @SerializedName("text")
        val text: String  // Human-readable duration
    )
    
    data class Distance(
        @SerializedName("value")
        val value: Long,  // Distance in meters
        
        @SerializedName("text")
        val text: String  // Human-readable distance
    )
    
    companion object {
        const val STATUS_OK = "OK"
        const val STATUS_ZERO_RESULTS = "ZERO_RESULTS"
        const val STATUS_MAX_ROUTE_LENGTH_EXCEEDED = "MAX_ROUTE_LENGTH_EXCEEDED"
    }
}
