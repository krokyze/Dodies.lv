package com.krokyze.dodies.view.favorites

import androidx.lifecycle.ViewModel
import com.krokyze.dodies.repository.LocationRepository

/**
 * Created by krokyze on 05/02/2018.
 */
class FavoritesViewModel(private val locationRepository: LocationRepository) : ViewModel() {

    fun getLocations() = locationRepository.getFavoriteLocations()
}
