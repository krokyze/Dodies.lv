package com.krokyze.dodies.repository.api

import com.google.gson.annotations.SerializedName

/**
 * Created by krokyze on 05/02/2018.
 */
data class LocationExtra(
        @SerializedName("image") val image: String,
        @SerializedName("title") val title: String,
        @SerializedName("desc-short") val description: String,
        @SerializedName("images") val images: List<String>)