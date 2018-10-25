package com.krokyze.dodies.view.location

import androidx.recyclerview.widget.OrientationHelper
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.annotations.LayoutSpec
import com.facebook.litho.annotations.OnCreateLayout
import com.facebook.litho.annotations.Prop
import com.facebook.litho.widget.ComponentRenderInfo
import com.facebook.litho.widget.LinearLayoutInfo
import com.facebook.litho.widget.Recycler
import com.facebook.litho.widget.RecyclerBinder

@LayoutSpec
object ImageViewPagerSpec {

    @OnCreateLayout
    @JvmStatic
    fun onCreateLayout(c: ComponentContext, @Prop images: List<String>): Component {
        val recyclerBinder = RecyclerBinder.Builder()
                .layoutInfo(LinearLayoutInfo(c, OrientationHelper.HORIZONTAL, false))
                .isCircular(true)
                .build(c)

        recyclerBinder.insertRangeAt(0, images
                .map { image ->
                    ImageItem.create(c)
                            .image(image)
                            .build()
                }
                .map { imageItem ->
                    ComponentRenderInfo.create()
                            .component(imageItem)
                            .build()
                })

        return Recycler.create(c)
                .aspectRatio(16f / 9f)
                // TODO
                // .snapHelper(PagerSnapHelper())
                .binder(recyclerBinder)
                .nestedScrollingEnabled(false)
                .build()
    }
}
