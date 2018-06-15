package com.krokyze.dodies.widget

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.AppCompatAutoCompleteTextView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import com.krokyze.dodies.R
import com.krokyze.dodies.repository.data.Location
import kotlinx.android.synthetic.main.location_auto_complete_list_item.view.*

/**
 * Created by krokyze on 24/09/2017.
 */

class LocationAutoCompleteTextView(context: Context, attrs: AttributeSet) : AppCompatAutoCompleteTextView(context, attrs) {

    var onLocationListener: ((Location) -> Unit)? = null

    private val adapter by lazy { LocationAdapter(context) }

    init {
        setAdapter(adapter)
        setOnItemClickListener { _, _, position, _ ->
            val location = adapter.getItem(position)

            setText(location.name)
            setSelection(text.length)

            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(windowToken, 0)
            clearFocus()

            onLocationListener?.invoke(location)
        }
    }

    fun setLocations(locations: List<Location>) {
        adapter.setLocations(locations)
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (!focused) {
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(windowToken, 0)
        }
    }

    internal class LocationAdapter(context: Context) : BaseAdapter(), Filterable {

        private val inflater = LayoutInflater.from(context)

        private val list = mutableListOf<Location>()
        private val filteredList = mutableListOf<Location>()

        override fun getCount(): Int = filteredList.size
        override fun getItem(index: Int) = filteredList[index]
        override fun getItemId(position: Int): Long = position.toLong()

        fun setLocations(locations: List<Location>) {
            list.clear()
            list.addAll(locations)
            notifyDataSetChanged()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view: View = convertView
                    ?: inflater.inflate(R.layout.location_auto_complete_list_item, parent, false)

            val location = getItem(position)

            val backgroundResource = when (location.status) {
                Location.Status.VERIFIED -> R.drawable.location_verified_background
                Location.Status.UNKNOWN -> R.drawable.location_background
            }
            val imageResource = when (location.type) {
                Location.Type.PICNIC -> R.drawable.ic_type_picnic
                Location.Type.TRAIL -> R.drawable.ic_type_trail
                Location.Type.TOWER -> R.drawable.ic_type_tower
                Location.Type.PARK -> R.drawable.ic_type_park
            }

            view.type.setBackgroundResource(backgroundResource)
            view.type.setImageResource(imageResource)
            view.name.text = location.name

            return view
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
                    return Filter.FilterResults().apply {
                        if (constraint != null) {
                            val items = list.filter { it.name.contains(constraint, true) }
                                    .sortedBy { it.name }

                            values = items
                            count = items.size
                        }

                    }
                }

                override fun publishResults(constraint: CharSequence?, results: Filter.FilterResults?) {
                    if (results != null && results.count > 0) {
                        filteredList.clear()
                        filteredList.addAll(results.values as List<Location>)
                        notifyDataSetChanged()
                    } else {
                        notifyDataSetInvalidated()
                    }
                }
            }
        }
    }
}
