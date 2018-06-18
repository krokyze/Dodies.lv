package com.krokyze.dodies.view.favorites.spec

import com.facebook.litho.annotations.Prop
import com.facebook.litho.sections.Children
import com.facebook.litho.sections.SectionContext
import com.facebook.litho.sections.annotations.GroupSectionSpec
import com.facebook.litho.sections.annotations.OnCreateChildren
import com.facebook.litho.sections.common.SingleComponentSection
import com.krokyze.dodies.repository.data.Location

/**
 * Created by krokyze on 14/03/2018.
 */
@GroupSectionSpec
object FavoritesListSectionSpec {

    @OnCreateChildren
    @JvmStatic
    fun onCreateChildren(c: SectionContext, @Prop locations: List<Location>, @Prop listener: FavoritesListItemSpec.OnLocationClickListener): Children {
        return Children.create()
                .children(locations.map { location ->
                    SingleComponentSection.create(c)
                            .key(location.url)
                            .component(FavoritesListItem.create(c)
                                    .location(location)
                                    .listener(listener))
                }).build()
    }
}