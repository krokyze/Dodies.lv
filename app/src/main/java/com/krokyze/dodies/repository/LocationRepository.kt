package com.krokyze.dodies.repository

import android.content.res.AssetManager
import com.google.gson.Gson
import com.krokyze.dodies.repository.api.LocationApi
import com.krokyze.dodies.repository.api.LocationsResponse
import com.krokyze.dodies.repository.data.Location
import com.krokyze.dodies.repository.db.LocationDao
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Created by krokyze on 05/02/2018.
 */
class LocationRepository(
    private val locationApi: LocationApi,
    private val locationDao: LocationDao,
    private val assetManager: AssetManager
) {

    fun getLocations(): Observable<List<Location>> {
        return Observable.concatArrayEager(getLocationsFromDb(), getLocationsFromApi())
                .debounce(400, TimeUnit.MILLISECONDS)
    }

    fun getLocation(url: String) = locationDao.getLocation(url)

    fun getLocationExtra(url: String) = locationApi.getLocationExtra(url)

    fun getFavoriteLocations() = locationDao.getFavoriteLocations()

    fun update(location: Location) {
        Observable.fromCallable { locationDao.update(location) }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {
                    Timber.d("${location.name} updated")
                }
    }

    private fun getLocationsFromDb(): Observable<List<Location>> {
        return locationDao.getLocations()
                .filter { it.isNotEmpty() }
                .toObservable()
                .doOnNext {
                    Timber.d("Dispatching ${it.size} locations from DB...")
                }
                .switchIfEmpty(getLocationsFromAssets())
    }

    private fun getLocationsFromApi(): Observable<List<Location>> {
        return locationApi.getLocations()
                .map { it.locations.map { Location(it) } }
                .doOnError {
                    Timber.e(it, "Failed to load locations from api")
                }
                .doOnNext {
                    Timber.d("Dispatching ${it.size} locations from API...")
                    saveLocationsInDb(it)
                }
                .materialize()
                .filter { !it.isOnError }
                .dematerialize<List<Location>>()
    }

    private fun getLocationsFromAssets(): Observable<List<Location>> {
        return Observable.just(assetManager.open("locations.json")
                .reader()
                .use { reader ->
                    Gson().fromJson(reader, LocationsResponse::class.java)
                            .locations.map { Location(it) }
                })
                .doOnNext {
                    Timber.d("Dispatching ${it.size} locations from Assets...")
                    saveLocationsInDb(it)
                }
    }

    private fun saveLocationsInDb(locations: List<Location>) {
        locationDao.getFavoriteLocations()
                .first(emptyList())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map { it.map { it.url } }
                .subscribe { favoriteIds ->
                    // save favorite information
                    favoriteIds.forEach { favoriteId ->
                        locations.firstOrNull { it.url == favoriteId }
                                ?.favorite = true
                    }

                    locationDao.deleteAll()
                    locationDao.insertAll(locations)
                    Timber.d("Inserted ${locations.size} locations in DB...")
                }
    }
}
