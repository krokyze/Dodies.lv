package com.krokyze.dodies.view.favorites

import android.content.Context
import android.graphics.Canvas
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import com.krokyze.dodies.R


/**
 * Created by krokyze on 05/02/2018.
 */
class FavoritesItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val divider by lazy { ResourcesCompat.getDrawable(context.resources, R.drawable.favorites_divider, null)!! }
    private val dividerPadding by lazy { context.resources.getDimensionPixelSize(R.dimen.favorites_divider_padding) }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft + dividerPadding
        val right = parent.width - parent.paddingRight - dividerPadding

        val childCount = Math.max(0, parent.childCount - 1)
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin
            val bottom = top + divider.intrinsicHeight

            divider.setBounds(left, top, right, bottom)
            divider.draw(canvas)
        }
    }
}