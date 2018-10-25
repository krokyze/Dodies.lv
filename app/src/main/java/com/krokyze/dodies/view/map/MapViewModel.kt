package com.krokyze.dodies.view.map

import androidx.lifecycle.ViewModel
import com.krokyze.dodies.repository.LocationRepository

/**
 * Created by krokyze on 05/02/2018.
 */
class MapViewModel(private val locationRepository: LocationRepository) : ViewModel() {

    fun getLocations() = locationRepository.getLocations()
}
