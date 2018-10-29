package com.krokyze.dodies.view.favorites.spec

import android.text.Html
import android.text.TextUtils
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.litho.Border
import com.facebook.litho.ClickEvent
import com.facebook.litho.Column
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.Row
import com.facebook.litho.annotations.LayoutSpec
import com.facebook.litho.annotations.OnCreateLayout
import com.facebook.litho.annotations.OnEvent
import com.facebook.litho.annotations.Prop
import com.facebook.litho.fresco.FrescoImage
import com.facebook.litho.widget.SolidColor
import com.facebook.litho.widget.Text
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaEdge
import com.krokyze.dodies.R
import com.krokyze.dodies.repository.data.Location

/**
 * Created by krokyze on 13/03/2018.
 */
@LayoutSpec
object FavoritesListItemSpec {

    @OnCreateLayout
    @JvmStatic
    fun onCreateLayout(c: ComponentContext, @Prop location: Location): Component {
        return Column.create(c)
                .child(Row.create(c)
                        .alignItems(YogaAlign.CENTER)
                        .border(Border.create(c)
                                .widthDip(YogaEdge.VERTICAL, 8)
                                .widthDip(YogaEdge.HORIZONTAL, 16)
                                .build())
                        .child(createImage(c, location)
                                ?.flexShrink(0f)
                                ?.marginDip(YogaEdge.END, 16f))
                        .child(Column.create(c)
                                .child(createTitle(c, location))
                                .child(createDescription(c, location)))
                        .clickHandler(FavoritesListItem.onLocationClick(c)))
                .child(createSeparator(c)
                        .paddingDip(YogaEdge.START, if (location.image.small.isEmpty()) 16f else 104f)
                        .paddingDip(YogaEdge.END, 16f))
                .build()
    }

    @OnEvent(ClickEvent::class)
    @JvmStatic
    fun onLocationClick(c: ComponentContext, @Prop location: Location, @Prop onLocationListener: (Location) -> Unit) {
        onLocationListener(location)
    }

    private fun createImage(c: ComponentContext, location: Location): FrescoImage.Builder? {
        if (location.image.small.isEmpty()) {
            return null
        }

        val controller = Fresco.newDraweeControllerBuilder()
                .setUri(location.image.small)
                .setTapToRetryEnabled(true)
                .build()

        return FrescoImage.create(c)
                .controller(controller)
                .actualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                .heightDip(72f)
                .widthDip(72f)
                .placeholderImageRes(R.drawable.ic_image)
                .failureImageRes(R.drawable.ic_image_failure)
                .retryImageRes(R.drawable.ic_refresh)
    }

    private fun createTitle(c: ComponentContext, location: Location): Text.Builder {
        var title = location.name
        if (location.distance.isNotEmpty()) {
            title += " "
            title += c.getString(R.string.distance_holder, location.distance)
        }

        return Text.create(c, 0, R.style.TextAppearance_AppCompat_Subhead)
                .text(title)
                .textColorRes(R.color.primary_text)
                .maxLines(1)
                .ellipsize(TextUtils.TruncateAt.END)
    }

    private fun createDescription(c: ComponentContext, location: Location): Text.Builder {
        return Text.create(c, 0, R.style.TextAppearance_AppCompat_Body1)
                .text(Html.fromHtml(location.text))
                .textColorRes(R.color.secondary_text)
                .maxLines(2)
                .ellipsize(TextUtils.TruncateAt.END)
    }

    private fun createSeparator(c: ComponentContext): SolidColor.Builder {
        return SolidColor.create(c)
                .colorRes(R.color.divider)
                .heightDip(1f)
    }
}
