package com.krokyze.dodies.view.map

import android.arch.lifecycle.ViewModel
import com.krokyze.dodies.App
import com.krokyze.dodies.repository.LocationRepository

/**
 * Created by krokyze on 05/02/2018.
 */
class MapViewModel : ViewModel() {

    private val locationRepository: LocationRepository by lazy { App.locationRepository }

    fun getLocations() = locationRepository.getLocations()
}