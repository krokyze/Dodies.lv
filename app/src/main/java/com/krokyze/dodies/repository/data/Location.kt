package com.krokyze.dodies.repository.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.krokyze.dodies.repository.api.LocationResponse

/**
 * Created by krokyze on 05/02/2018.
 */
@Entity(tableName = "locations")
data class Location(@PrimaryKey
                    @ColumnInfo(name = "name") val name: String,
                    val type: Type,
                    val url: String,
                    @Embedded val image: Image,
                    @Embedded val coordinates: Coordinates,
                    val text: String,
                    val status: Status,
                    val distance: String,
                    val date: String) : ClusterItem {

    @ColumnInfo(name = "favorite")
    var favorite: Boolean = false

    override fun getTitle() = name

    override fun getPosition() = LatLng(coordinates.latitude, coordinates.longitude)

    override fun getSnippet() = text

    data class Image(val small: String,
                     val large: String)

    data class Coordinates(val latitude: Double,
                           val longitude: Double)

    enum class Type(val value: String) {
        PICNIC("pikniks"),
        TRAIL("taka"),
        TOWER("tornis"),
        PARK("parks");

        companion object {
            fun fromString(value: String): Type {
                return Type.values().firstOrNull { it.value.equals(value, true) }
                        ?: throw IllegalArgumentException("unknown Location.Type: $value")
            }
        }
    }

    enum class Status(val value: String) {
        VERIFIED("parbaudits"),
        UNKNOWN("");

        companion object {
            fun fromString(value: String): Status {
                return Status.values()
                        .firstOrNull { it.value.equals(value, true) } ?: UNKNOWN
            }
        }
    }


    // constructor to map api location structure to db structure
    constructor(location: LocationResponse.Location) :
            this(name = location.properties.name,
                    type = location.properties.type.let { Type.fromString(it) },
                    url = location.properties.url,
                    image = location.properties.let { Image(it.imgSmall, it.imgLarge) },
                    coordinates = location.geometry.coordinates.let { Coordinates(it[1], it[0]) },
                    text = location.properties.text,
                    status = location.properties.status.let { Status.fromString(it) },
                    distance = location.properties.distance,
                    date = location.properties.date)
}