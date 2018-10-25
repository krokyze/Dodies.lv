package com.krokyze.dodies.view.favorites

import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.facebook.litho.Column
import com.facebook.litho.sections.SectionContext
import com.facebook.litho.sections.widget.RecyclerCollectionComponent
import com.facebook.litho.widget.Text
import com.facebook.yoga.YogaJustify
import com.krokyze.dodies.R
import com.krokyze.dodies.repository.data.Location
import com.krokyze.dodies.view.favorites.spec.FavoritesListItemSpec
import com.krokyze.dodies.view.favorites.spec.FavoritesListSection
import com.krokyze.dodies.view.location.LocationFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_favorites.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Created by krokyze on 05/02/2018.
 */
class FavoritesFragment : Fragment() {

    private val viewModel by viewModel<FavoritesViewModel>()
    private var disposable: Disposable? = null

    private val sectionContext by lazy { SectionContext(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disposable = viewModel.getLocations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { location -> onLocations(location) }
    }

    private fun onLocations(locations: List<Location>) {
        if (locations.isEmpty()) {
            litho_view.setComponentAsync(Column.create(sectionContext)
                    .justifyContent(YogaJustify.CENTER)
                    .child(Text.create(sectionContext, 0, R.style.TextAppearance_AppCompat_Body1)
                            .textRes(R.string.favorites_empty)
                            .textColorRes(R.color.primary_text)
                            .textAlignment(Layout.Alignment.ALIGN_CENTER))
                    .build())
        } else {
            litho_view.setComponentAsync(RecyclerCollectionComponent
                    .create(sectionContext)
                    .topPaddingDip(8f)
                    .bottomPaddingDip(8f)
                    .clipToPadding(false)
                    .section(
                            FavoritesListSection.create(sectionContext)
                                    .locations(locations)
                                    .listener(object : FavoritesListItemSpec.OnLocationClickListener {
                                        override fun onLocationClick(location: Location) {
                                            LocationFragment.newInstance(location)
                                                    .show(childFragmentManager, null)
                                        }
                                    })
                    )
                    .disablePTR(true)
                    .build())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable?.dispose()
    }
}
