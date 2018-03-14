package com.krokyze.dodies.view.location

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.text.Layout
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.litho.*
import com.facebook.litho.annotations.LayoutSpec
import com.facebook.litho.annotations.OnCreateLayout
import com.facebook.litho.annotations.OnEvent
import com.facebook.litho.annotations.Prop
import com.facebook.litho.fresco.FrescoImage
import com.facebook.litho.widget.Card
import com.facebook.litho.widget.Image
import com.facebook.litho.widget.Text
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaEdge
import com.facebook.yoga.YogaJustify
import com.krokyze.dodies.R
import com.krokyze.dodies.repository.data.Location


/**
 * Created by krokyze on 13/03/2018.
 */
@LayoutSpec
object LocationComponentSpec {

    @OnCreateLayout
    @JvmStatic
    fun onCreateLayout(c: ComponentContext, @Prop location: Location): Component {
        return Column.create(c)
                .child(createImage(c, location))
                .child(Column.create(c)
                        .border(Border.create(c)
                                .widthDip(YogaEdge.START, 16)
                                .widthDip(YogaEdge.TOP, 24)
                                .widthDip(YogaEdge.END, 16)
                                .widthDip(YogaEdge.BOTTOM, 24)
                                .build())
                        .child(createTitle(c, location))
                        .child(createCoordinates(c, location).marginDip(YogaEdge.TOP, 16f))
                        .child(createDate(c, location)?.marginDip(YogaEdge.TOP, 2f))
                        .child(createDescription(c, location).marginDip(YogaEdge.TOP, 16f))
                        .child(createLoad(c).marginDip(YogaEdge.TOP, 24f)))
                .build()
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
                .imageAspectRatio(16f / 9f) // 16:9
                .placeholderImageRes(R.drawable.ic_image)
                .failureImageRes(R.drawable.ic_image_failure)
                .retryImageRes(R.drawable.ic_refresh)
    }

    private fun createTitle(c: ComponentContext, location: Location): Row.Builder {
        var title = location.name
        if (location.distance.isNotEmpty()) {
            title += " "
            title += c.getString(R.string.distance_holder, location.distance)
        }

        return Row.create(c)
                .justifyContent(YogaJustify.SPACE_BETWEEN)
                .alignItems(YogaAlign.CENTER)
                .child(
                        Text.create(c, 0, R.style.TextAppearance_AppCompat_Title)
                                .text(title)
                                .textColorRes(R.color.primary_text)
                )
                .child(
                        Image.create(c)
                                .marginDip(YogaEdge.START, 4f)
                                .drawableRes(if (location.favorite) R.drawable.ic_favorite_selected else R.drawable.ic_favorite)
                                .clickHandler(LocationComponent.onFavoriteClick(c))
                )
    }

    interface OnFavoriteClickListener {
        fun onFavorite()
    }

    @OnEvent(ClickEvent::class)
    @JvmStatic
    fun onFavoriteClick(c: ComponentContext, @Prop favoriteListener: OnFavoriteClickListener) {
        favoriteListener.onFavorite()
    }

    private fun createCoordinates(c: ComponentContext, location: Location): Row.Builder {
        return Row.create(c)
                .child(
                        Text.create(c, 0, R.style.TextAppearance_AppCompat_Body2)
                                .textRes(R.string.coordinates_title)
                                .textColorRes(R.color.primary_text)
                )
                .child(
                        Text.create(c, 0, R.style.TextAppearance_AppCompat_Body1)
                                .text(listOf(location.coordinates.latitude, location.coordinates.longitude).joinToString())
                                .textColorRes(R.color.colorPrimary)
                                .clickHandler(LocationComponent.onCoordinatesClick(c))
                                .marginDip(YogaEdge.START, 4f)
                )
    }

    @OnEvent(ClickEvent::class)
    @JvmStatic
    fun onCoordinatesClick(c: ComponentContext, @Prop location: Location) {
        AlertDialog.Builder(c)
                .setItems(R.array.coordinates_dialog_items, { _, index ->
                    val locationString = "${location.coordinates.latitude},${location.coordinates.longitude}"
                    when (index) {
                        0 -> {
                            val clipboardManager = c.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboardManager.primaryClip = ClipData.newPlainText(location.name, locationString)
                        }
                        else -> {
                            val uri = Uri.parse("geo:$locationString")
                            c.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        }
                    }
                })
                .show()
    }

    private fun createDate(c: ComponentContext, location: Location): Row.Builder? {
        if (location.date.isEmpty()) {
            return null
        }

        return Row.create(c)
                .child(
                        Text.create(c, 0, R.style.TextAppearance_AppCompat_Body2)
                                .textRes(R.string.date_title)
                                .textColorRes(R.color.primary_text)
                )
                .child(
                        Text.create(c, 0, R.style.TextAppearance_AppCompat_Body1)
                                .text(location.date)
                                .textColorRes(R.color.primary_text)
                                .marginDip(YogaEdge.START, 4f)
                )
    }

    private fun createDescription(c: ComponentContext, location: Location): Text.Builder {
        return Text.create(c, 0, R.style.TextAppearance_AppCompat_Body1)
                .text(location.text)
                .textColorRes(R.color.primary_text)
    }

    private fun createLoad(c: ComponentContext): Column.Builder {
        return Column.create(c)
                .alignItems(YogaAlign.CENTER)
                .justifyContent(YogaJustify.CENTER)
                .child(Card.create(c)
                        .cardBackgroundColorRes(R.color.colorPrimary)
                        .cornerRadiusDip(2f)
                        .elevationDip(2f)
                        .content(Text.create(c, 0, R.style.TextAppearance_AppCompat_Button)
                                .textRes(R.string.see_more)
                                .textColorRes(android.R.color.white)
                                .textAlignment(Layout.Alignment.ALIGN_CENTER)
                                .alignSelf(YogaAlign.STRETCH)
                                .paddingDip(YogaEdge.ALL, 8f)
                                .build())
                        .clickHandler(LocationComponent.onSeeMoreClick(c))
                        .flexShrink(1f)
                        .alignSelf(YogaAlign.CENTER))
    }

    @OnEvent(ClickEvent::class)
    @JvmStatic
    fun onSeeMoreClick(c: ComponentContext, @Prop location: Location) {
        val uri = Uri.parse("https://dodies.lv/obj/${location.url}")
        c.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}