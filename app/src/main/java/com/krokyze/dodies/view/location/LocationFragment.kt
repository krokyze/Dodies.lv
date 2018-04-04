package com.krokyze.dodies.view.location

import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.ComponentTree
import com.krokyze.dodies.R
import com.krokyze.dodies.lazyFast
import com.krokyze.dodies.repository.data.Location
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_location.*


/**
 * Created by krokyze on 05/02/2018.
 */
class LocationFragment : BottomSheetDialogFragment() {

    private val viewModel by lazyFast {
        val locationId = arguments!!.getString(LOCATION_ID)
        val factory = LocationViewModel.Factory(locationId)
        ViewModelProviders.of(this, factory).get(locationId, LocationViewModel::class.java)
    }

    private var disposable: Disposable? = null

    private val componentContext by lazy { ComponentContext(requireContext()) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener { dialogInterface ->
                val dialog = dialogInterface as BottomSheetDialog
                val bottomSheet = dialog.findViewById<View>(android.support.design.R.id.design_bottom_sheet)
                BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        disposable = viewModel.getLocation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { location -> onLocation(location) }
    }

    private fun onLocation(location: Location) {
        if (litho_view.componentTree == null) {
            litho_view.componentTree = ComponentTree.create(componentContext, createLocationComponent(location))
                    .apply { incrementalMount(false) }
                    .build()
        } else {
            litho_view.setComponentAsync(createLocationComponent(location))
        }
    }

    private fun createLocationComponent(location: Location): Component {
        return LocationComponent.create(componentContext)
                .location(location)
                .favoriteListener(object : LocationComponentSpec.OnFavoriteClickListener {
                    override fun onFavorite() {
                        viewModel.onFavorite(location)
                    }
                })
                .build()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable?.dispose()
    }

    companion object {
        private const val LOCATION_ID = "location_id"

        fun newInstance(location: Location): LocationFragment {
            val bundle = Bundle()
            bundle.putString(LOCATION_ID, location.id)
            return LocationFragment().apply { arguments = bundle }
        }
    }
}