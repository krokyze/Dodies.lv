package com.krokyze.dodies.repository.api

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by krokyze on 05/02/2018.
 */
interface LocationApi {
    @GET("json/lv.geojson")
    fun getLocations(): Observable<LocationsResponse>

    @GET("{location}?json=1")
    fun getLocationExtra(@Path("location") url: String): Observable<LocationExtra>
}