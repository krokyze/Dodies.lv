package com.krokyze.dodies.view.location

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.krokyze.dodies.App
import com.krokyze.dodies.repository.LocationRepository
import com.krokyze.dodies.repository.data.Location

/**
 * Created by krokyze on 05/02/2018.
 */
class LocationViewModel(private val locationId: String) : ViewModel() {

    private val locationRepository: LocationRepository by lazy { App.locationRepository }

    fun getLocation() = locationRepository.getLocation(locationId)

    fun onFavorite(location: Location) {
        location.favorite = !location.favorite
        locationRepository.update(location)
    }

    class Factory(private val locationId: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                LocationViewModel(locationId) as T
    }
}