package com.krokyze.dodies.di

import com.krokyze.dodies.view.favorites.FavoritesViewModel
import com.krokyze.dodies.view.location.LocationViewModel
import com.krokyze.dodies.view.map.MapViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModule = module {
    viewModel { MapViewModel(get()) }
    viewModel { FavoritesViewModel(get()) }
    viewModel { (url: String) -> LocationViewModel(url, get()) }
}
