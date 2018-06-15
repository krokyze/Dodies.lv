package com.krokyze.dodies.di

import com.krokyze.dodies.view.favorites.FavoritesViewModel
import com.krokyze.dodies.view.location.LocationViewModel
import com.krokyze.dodies.view.map.MapViewModel
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

val viewModule = applicationContext {
    viewModel { MapViewModel(get()) }
    viewModel { FavoritesViewModel(get()) }
    viewModel { params -> LocationViewModel(params["url"], get()) }
}
