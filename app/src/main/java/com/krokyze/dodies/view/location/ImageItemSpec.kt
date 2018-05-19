package com.krokyze.dodies.view.location

import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.annotations.LayoutSpec
import com.facebook.litho.annotations.OnCreateLayout
import com.facebook.litho.annotations.Prop
import com.facebook.litho.fresco.FrescoImage
import com.krokyze.dodies.R


@LayoutSpec
object ImageItemSpec {

    @OnCreateLayout
    @JvmStatic
    fun onCreateLayout(c: ComponentContext, @Prop image: String): Component {
        val controller = Fresco.newDraweeControllerBuilder()
                .setUri(image)
                .setTapToRetryEnabled(true)
                .build()

        return FrescoImage.create(c)
                .controller(controller)
                .actualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                .imageAspectRatio(16f / 9f)
                .placeholderImageRes(R.drawable.ic_image)
                .failureImageRes(R.drawable.ic_image_failure)
                .retryImageRes(R.drawable.ic_refresh)
                .build()
    }

}