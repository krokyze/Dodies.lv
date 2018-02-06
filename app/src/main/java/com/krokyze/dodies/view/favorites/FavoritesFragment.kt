package com.krokyze.dodies.view.favorites

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.krokyze.dodies.R
import com.krokyze.dodies.repository.data.Location
import com.krokyze.dodies.view.location.LocationFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_favorites.*

/**
 * Created by krokyze on 05/02/2018.
 */
class FavoritesFragment : Fragment(), FavoritesAdapter.OnLocationClickListener {

    private lateinit var viewModel: FavoritesViewModel
    private var disposable: Disposable? = null

    private val adapter by lazy { FavoritesAdapter(this) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(context)
        recycler_view.addItemDecoration(FavoritesItemDecoration(context!!))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FavoritesViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        disposable = viewModel.getLocations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { location -> onLocations(location) }

    }

    private fun onLocations(locations: List<Location>) {
        adapter.setLocations(locations)

        if (locations.isEmpty()) {
            empty_view.visibility = View.VISIBLE
        } else {
            empty_view.visibility = View.GONE
        }
    }

    override fun onLocationClick(location: Location) {
        LocationFragment.newInstance(location)
                .show(childFragmentManager, null)
    }

    override fun onStop() {
        super.onStop()
        disposable?.dispose()
    }


}