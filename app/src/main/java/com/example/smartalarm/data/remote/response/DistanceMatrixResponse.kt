package com.example.smartalarm.data.remote.response

import com.google.gson.annotations.SerializedName

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
        val value: Long, // Duration in seconds
        @SerializedName("text")
        val text: String
    )

    data class Distance(
        @SerializedName("value")
        val value: Long, // Distance in meters
        @SerializedName("text")
        val text: String
    )
}
