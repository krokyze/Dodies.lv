package com.krokyze.dodies.repository

import com.krokyze.dodies.repository.api.LocationApi
import com.krokyze.dodies.repository.data.Location
import com.krokyze.dodies.repository.db.LocationDao
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Created by krokyze on 05/02/2018.
 */
class LocationRepository(private val locationApi: LocationApi,
                         private val locationDao: LocationDao) {

    fun getLocations(): Observable<List<Location>> {
        // Drop DB data if we can fetch from the API
        return Observable.concatArrayEager(
                getLocationsFromDb(),
                getLocationsFromApi()
                        .materialize()
                        .filter { !it.isOnError }
                        .dematerialize<List<Location>>()
                        .debounce(400, TimeUnit.MILLISECONDS)
        )
    }

    fun getLocation(name: String) = locationDao.getLocation(name)

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
    }

    private fun saveLocationsInDb(locations: List<Location>) {
        locationDao.getFavoriteLocations()
                .first(emptyList())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map { it.map { it.name } }
                .subscribe { favoriteLocations ->
                    // save favorite information
                    locations.forEach { it.favorite = favoriteLocations.contains(it.name) }

                    locationDao.deleteAll()
                    locationDao.insertAll(locations)
                    Timber.d("Inserted ${locations.size} locations from API in DB...")
                }
    }


}