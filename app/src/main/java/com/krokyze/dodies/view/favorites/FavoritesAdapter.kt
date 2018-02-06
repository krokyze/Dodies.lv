package com.krokyze.dodies.view.favorites

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.krokyze.dodies.R
import com.krokyze.dodies.repository.data.Location
import kotlinx.android.synthetic.main.list_location.view.*


/**
 * Created by krokyze on 05/02/2018.
 */
class FavoritesAdapter(private val clickListener: OnLocationClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val locations: MutableList<Location> = mutableListOf()

    fun setLocations(locations: List<Location>) {
        this.locations.clear()
        this.locations.addAll(locations)
        notifyDataSetChanged()
    }

    interface OnLocationClickListener {
        fun onLocationClick(location: Location)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun getItemCount() = locations.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_location, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val location = locations[position]

        with(holder.itemView) {
            if (location.image.small.isNotEmpty()) {
                image_view.visibility = View.VISIBLE
                Glide.with(image_view)
                        .load(location.image.small)
                        .into(image_view)
            } else {
                image_view.visibility = View.GONE
            }

            title_text_view.text = location.name
            if (location.distance.isNotEmpty()) {
                title_text_view.append(" ")
                title_text_view.append(resources.getString(R.string.distance_holder, location.distance))
            }

            if (location.text.isNotEmpty()) {
                description_text_view.visibility = View.VISIBLE
                description_text_view.text = location.text
            } else {
                description_text_view.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                clickListener.onLocationClick(location)
            }
        }
    }


}