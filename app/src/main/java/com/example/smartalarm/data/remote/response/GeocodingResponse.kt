package com.example.smartalarm.data.remote.response

import com.google.gson.annotations.SerializedName

data class GeocodingResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("results")
    val results: List<GeocodingResult>
) {
    data class GeocodingResult(
        @SerializedName("formatted_address")
        val formattedAddress: String,
        @SerializedName("geometry")
        val geometry: Geometry,
        @SerializedName("address_components")
        val addressComponents: List<AddressComponent>
    )

    data class Geometry(
        @SerializedName("location")
        val location: Location,
        @SerializedName("location_type")
        val locationType: String
    )

    data class Location(
        @SerializedName("lat")
        val lat: Double,
        @SerializedName("lng")
        val lng: Double
    )

    data class AddressComponent(
        @SerializedName("long_name")
        val longName: String,
        @SerializedName("short_name")
        val shortName: String,
        @SerializedName("types")
        val types: List<String>
    )
}
