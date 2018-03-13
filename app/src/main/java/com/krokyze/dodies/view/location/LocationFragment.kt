package com.krokyze.dodies.view.location

import android.arch.lifecycle.ViewModelProviders
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.app.AlertDialog
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.krokyze.dodies.R
import com.krokyze.dodies.repository.data.Location
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_location.*


/**
 * Created by krokyze on 05/02/2018.
 */
class LocationFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: LocationViewModel
    private var disposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val locationId = arguments!!.getString(LOCATION_ID)

        val factory = LocationViewModel.Factory(locationId)
        viewModel = ViewModelProviders.of(this, factory).get(locationId, LocationViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        disposable = viewModel.getLocation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { location -> onLocation(location) }
    }

    private fun onLocation(location: Location) {
        if (location.image.small.isNotEmpty()) {
            image_view.visibility = View.VISIBLE
            Glide.with(image_view)
                    .load(location.image.small)
                    .into(image_view)
        } else {
            image_view.visibility = View.GONE
        }

        title_text_view.text = location.name
        if (location.distance.isNotEmpty()) {
            title_text_view.append(" ")
            title_text_view.append(getString(R.string.distance_holder, location.distance))
        }

        if (location.favorite) {
            favorite_image_view.setImageResource(R.drawable.ic_favorite_selected)
        } else {
            favorite_image_view.setImageResource(R.drawable.ic_favorite)
        }
        favorite_image_view.setOnClickListener { viewModel.onFavorite(location) }

        coordinates_text_view.text = Html.fromHtml(getString(R.string.coordinates_holder, location.coordinates.latitude, location.coordinates.longitude))
        coordinates_text_view.setOnClickListener {
            AlertDialog.Builder(requireContext())
                    .setItems(R.array.coordinates_dialog_items, { _, index ->
                        val locationString = "${location.coordinates.latitude},${location.coordinates.longitude}"
                        when (index) {
                            0 -> {
                                val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboardManager.primaryClip = ClipData.newPlainText(location.name, locationString)
                            }
                            1 -> {
                                val uri = Uri.parse("geo:$locationString")
                                startActivity(Intent(Intent.ACTION_VIEW, uri))
                            }
                        }
                    })
                    .show()

        }

        if (location.date.isNotEmpty()) {
            date_text_view.visibility = View.VISIBLE
            date_text_view.text = getString(R.string.date_holder, location.date)
        } else {
            date_text_view.visibility = View.GONE
        }

        if (location.text.isNotEmpty()) {
            description_text_view.visibility = View.VISIBLE
            description_text_view.text = location.text
        } else {
            description_text_view.visibility = View.GONE
        }

        see_more_button.setOnClickListener {
            val uri = Uri.parse("https://dodies.lv/obj/${location.url}")
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    override fun onStop() {
        super.onStop()
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