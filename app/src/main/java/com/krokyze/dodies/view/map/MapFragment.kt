package com.krokyze.dodies.view.map

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import com.krokyze.dodies.R
import com.krokyze.dodies.lazyFast
import com.krokyze.dodies.repository.data.Location
import com.krokyze.dodies.view.location.LocationFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_map.*
import timber.log.Timber


class MapFragment : Fragment(), OnMapReadyCallback {

    private val viewModel by lazyFast { ViewModelProviders.of(this).get(MapViewModel::class.java) }
    private var disposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)

        about.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_url)))
            startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val clusterManager = ClusterManager<Location>(requireContext(), googleMap)
        clusterManager.renderer = ClusterRenderer(requireContext(), googleMap, clusterManager)
        clusterManager.setOnClusterItemInfoWindowClickListener { location ->
            LocationFragment.newInstance(location)
                    .show(childFragmentManager, null)
        }

        googleMap.setOnCameraIdleListener(clusterManager)
        googleMap.setOnMarkerClickListener(clusterManager)
        googleMap.setOnInfoWindowClickListener(clusterManager)

        // center of Riga
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(56.946285, 24.105078), 8f))

        with(googleMap.uiSettings) {
            isZoomControlsEnabled = true
            isScrollGesturesEnabled = true
            isRotateGesturesEnabled = false
            isTiltGesturesEnabled = false
        }

        disposable = viewModel.getLocations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    clusterManager.clearItems()
                    clusterManager.addItems(it)
                    clusterManager.cluster()
                }, {
                    Timber.w(it)
                    // don't show any error, cause db is already filled with items
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}
