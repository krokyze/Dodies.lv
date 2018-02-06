package com.krokyze.dodies.repository.api

import com.google.gson.annotations.SerializedName

/**
 * Created by krokyze on 05/02/2018.
 */
data class LocationResponse(
        @SerializedName("features")
        val locations: List<Location>) {

    data class Location(
            @SerializedName("properties") val properties: Properties,
            @SerializedName("geometry") val geometry: Geometry) {

        data class Properties(
                @SerializedName("name") val name: String,
                @SerializedName("tips") val type: String,
                @SerializedName("url") val url: String,
                @SerializedName("img") val imgSmall: String,
                @SerializedName("img2") val imgLarge: String,
                @SerializedName("txt") val text: String,
                @SerializedName("st") val status: String,
                @SerializedName("km") val distance: String,
                @SerializedName("dat") val date: String)

        // [0] - Longitude
        // [1] - Latitude
        data class Geometry(@SerializedName("coordinates") val coordinates: List<Double>)
    }
}