package com.krokyze.dodies.view.location

import android.arch.lifecycle.ViewModel
import com.krokyze.dodies.repository.LocationRepository
import com.krokyze.dodies.repository.api.LocationExtra
import com.krokyze.dodies.repository.api.NetworkRequest
import com.krokyze.dodies.repository.data.Location
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Flowables
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by krokyze on 05/02/2018.
 */
class LocationViewModel(
    private val locationUrl: String,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val locationExtra = BehaviorSubject.create<NetworkRequest<LocationExtra>>()

    private var disposable: Disposable? = null

    fun getLocation(): Flowable<Pair<Location, NetworkRequest<LocationExtra>>> {
        onSeeMore()
        return Flowables.combineLatest(locationRepository.getLocation(locationUrl), locationExtra.toFlowable(BackpressureStrategy.LATEST))
    }

    fun onSeeMore() {
        locationExtra.onNext(NetworkRequest.loading(true))

        disposable?.dispose()
        disposable = locationRepository.getLocationExtra(locationUrl)
                .subscribeOn(Schedulers.io())
                .subscribe({ response ->
                    locationExtra.onNext(NetworkRequest.success(response))
                }, { error ->
                    locationExtra.onNext(NetworkRequest.failure(error))
                })
    }

    fun onFavorite(location: Location) {
        location.favorite = !location.favorite
        locationRepository.update(location)
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }
}
