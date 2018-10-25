package com.krokyze.dodies.view.location

import com.facebook.litho.annotations.Prop
import com.facebook.litho.sections.Children
import com.facebook.litho.sections.SectionContext
import com.facebook.litho.sections.annotations.GroupSectionSpec
import com.facebook.litho.sections.annotations.OnCreateChildren
import com.facebook.litho.sections.common.SingleComponentSection

@GroupSectionSpec
object ImageViewPagerSectionSpec {

    @OnCreateChildren
    @JvmStatic
    fun onCreateChildren(c: SectionContext, @Prop images: List<String>): Children {
        return Children.create()
                .children(images.map { image ->
                    SingleComponentSection.create(c)
                            .key(image)
                            .component(ImageItem.create(c)
                                    .image(image))
                }).build()
    }
}
