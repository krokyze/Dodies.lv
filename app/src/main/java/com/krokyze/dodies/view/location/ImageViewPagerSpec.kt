package com.krokyze.dodies.view.location

import androidx.recyclerview.widget.OrientationHelper
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.annotations.LayoutSpec
import com.facebook.litho.annotations.OnCreateLayout
import com.facebook.litho.annotations.Prop
import com.facebook.litho.sections.SectionContext
import com.facebook.litho.sections.widget.ListRecyclerConfiguration
import com.facebook.litho.sections.widget.RecyclerBinderConfiguration
import com.facebook.litho.sections.widget.RecyclerCollectionComponent
import com.facebook.litho.widget.SnapUtil

@LayoutSpec
object ImageViewPagerSpec {

    @OnCreateLayout
    @JvmStatic
    fun onCreateLayout(c: ComponentContext, @Prop images: List<String>): Component {
        return RecyclerCollectionComponent.create(c)
                .aspectRatio(16f / 9f)
                .section(
                        ImageViewPagerSection.create(SectionContext(c))
                                .images(images)
                                .build()
                )
                .recyclerConfiguration(
                        ListRecyclerConfiguration.create()
                                .orientation(OrientationHelper.HORIZONTAL)
                                .snapMode(SnapUtil.SNAP_TO_CENTER)
                                .recyclerBinderConfiguration(
                                        RecyclerBinderConfiguration.create()
                                                .isCircular(true)
                                                .build()
                                )
                                .build()
                )
                .nestedScrollingEnabled(false)
                .clipToPadding(false)
                .disablePTR(true)
                .build()
    }
}
