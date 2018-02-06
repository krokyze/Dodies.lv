package com.krokyze.dodies.repository.api

import io.reactivex.Observable
import retrofit2.http.GET

/**
 * Created by krokyze on 05/02/2018.
 */
interface LocationApi {
    @GET("json/lv.geojson")
    fun getLocations(): Observable<LocationResponse>
}