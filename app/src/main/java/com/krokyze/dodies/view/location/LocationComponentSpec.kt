package com.krokyze.dodies.view.location

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Html
import android.text.Layout
import androidx.appcompat.app.AlertDialog
import com.facebook.litho.*
import com.facebook.litho.annotations.LayoutSpec
import com.facebook.litho.annotations.OnCreateLayout
import com.facebook.litho.annotations.OnEvent
import com.facebook.litho.annotations.Prop
import com.facebook.litho.widget.Card
import com.facebook.litho.widget.Image
import com.facebook.litho.widget.Progress
import com.facebook.litho.widget.Text
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaEdge
import com.facebook.yoga.YogaJustify
import com.krokyze.dodies.R
import com.krokyze.dodies.repository.api.LocationExtra
import com.krokyze.dodies.repository.api.NetworkRequest
import com.krokyze.dodies.repository.data.Location

/**
 * Created by krokyze on 13/03/2018.
 */
@LayoutSpec
object LocationComponentSpec {

    @OnCreateLayout
    @JvmStatic
    fun onCreateLayout(c: ComponentContext, @Prop location: Location, @Prop locationExtra: NetworkRequest<LocationExtra>): Component {
        return Column.create(c)
                .child(createImage(c, location, locationExtra))
                .child(Column.create(c)
                        .border(Border.create(c)
                                .widthDip(YogaEdge.START, 16)
                                .widthDip(YogaEdge.TOP, 24)
                                .widthDip(YogaEdge.END, 16)
                                .widthDip(YogaEdge.BOTTOM, 24)
                                .build())
                        .child(createTitle(c, location, locationExtra))
                        .child(createCoordinates(c, location).marginDip(YogaEdge.TOP, 16f))
                        .child(createDate(c, location)?.marginDip(YogaEdge.TOP, 2f))
                        .child(createDescription(c, location, locationExtra).marginDip(YogaEdge.TOP, 16f))
                        .child(createLoad(c, locationExtra)?.marginDip(YogaEdge.TOP, 24f)))
                .build()
    }

    private fun createImage(c: ComponentContext, location: Location, locationExtra: NetworkRequest<LocationExtra>): Component? {
        if (location.image.small.isEmpty() && (locationExtra !is NetworkRequest.Success || locationExtra.data.images.isEmpty())) {
            return null
        }

        if (locationExtra is NetworkRequest.Success && locationExtra.data.images.size > 1) {
            return ImageViewPager.create(c)
                    .images(locationExtra.data.images)
                    .build()
        }

        return ImageItem.create(c)
                .image((locationExtra as? NetworkRequest.Success)?.data?.images?.firstOrNull()
                        ?: location.image.small)
                .build()
    }

    private fun createTitle(c: ComponentContext, location: Location, locationExtra: NetworkRequest<LocationExtra>): Row.Builder {
        var title = (locationExtra as? NetworkRequest.Success)?.data?.title ?: location.name
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
                .child(Row.create(c)
                        .marginDip(YogaEdge.START, 4f)
                        .child(
                                Image.create(c)
                                        .paddingDip(YogaEdge.ALL, 4f)
                                        .drawableRes(if (location.favorite) R.drawable.ic_favorite_selected else R.drawable.ic_favorite)
                                        .clickHandler(LocationComponent.onFavoriteClick(c))
                        )
                        .child(
                                Image.create(c)
                                        .paddingDip(YogaEdge.ALL, 4f)
                                        .drawableRes(R.drawable.ic_share)
                                        .clickHandler(LocationComponent.onShareClick(c))
                        ))
    }

    interface OnFavoriteClickListener {
        fun onFavorite()
    }

    @OnEvent(ClickEvent::class)
    @JvmStatic
    fun onFavoriteClick(c: ComponentContext, @Prop onFavoriteClickListener: OnFavoriteClickListener) {
        onFavoriteClickListener.onFavorite()
    }

    @OnEvent(ClickEvent::class)
    @JvmStatic
    fun onShareClick(c: ComponentContext, @Prop location: Location) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "https://dodies.lv${location.url}")
        }
        c.startActivity(Intent.createChooser(intent, c.getString(R.string.share)))
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
                .setItems(R.array.coordinates_dialog_items) { _, index ->
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
                }
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

    private fun createDescription(c: ComponentContext, location: Location, locationExtra: NetworkRequest<LocationExtra>): Text.Builder {
        val description = (locationExtra as? NetworkRequest.Success)?.data?.description
                ?: location.text

        return Text.create(c, 0, R.style.TextAppearance_AppCompat_Body1)
                .text(Html.fromHtml(description))
                .extraSpacingDip(4f)
                .textColorRes(R.color.primary_text)
    }

    private fun createLoad(c: ComponentContext, locationExtra: NetworkRequest<LocationExtra>): Column.Builder? {
        if (locationExtra is NetworkRequest.Success) {
            return null
        }

        return Column.create(c)
                .alignItems(YogaAlign.CENTER)
                .justifyContent(YogaJustify.CENTER)
                .child(createLoadChild(c, locationExtra))
    }

    private fun createLoadChild(c: ComponentContext, locationExtra: NetworkRequest<LocationExtra>): Component {
        if (locationExtra is NetworkRequest.Progress) {
            return Progress.create(c)
                    .widthDip(42f)
                    .heightDip(42f)
                    .alignSelf(YogaAlign.CENTER)
                    .build()
        }

        return Card.create(c)
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
                .alignSelf(YogaAlign.CENTER)
                .build()
    }

    interface OnSeeMoreClickListener {
        fun onSeeMore()
    }

    @OnEvent(ClickEvent::class)
    @JvmStatic
    fun onSeeMoreClick(c: ComponentContext, @Prop onSeeMoreClickListener: OnSeeMoreClickListener) {
        onSeeMoreClickListener.onSeeMore()
    }
}
