package com.krokyze.dodies.repository.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.krokyze.dodies.repository.api.LocationsResponse

/**
 * Created by krokyze on 05/02/2018.
 */
@Entity(tableName = "locations")
data class Location(
        @PrimaryKey
        @ColumnInfo(name = "url") val url: String,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "type") val type: Type,
        @Embedded val image: Image,
        @Embedded val coordinates: Coordinates,
        @ColumnInfo(name = "text") val text: String,
        @ColumnInfo(name = "status") val status: Status,
        @ColumnInfo(name = "distance") val distance: String,
        @ColumnInfo(name = "date") val date: String
) : ClusterItem {

    @ColumnInfo(name = "favorite")
    var favorite: Boolean = false

    override fun getTitle() = name

    override fun getPosition() = LatLng(coordinates.latitude, coordinates.longitude)

    override fun getSnippet() = text

    data class Image(
            @ColumnInfo(name = "small") val small: String,
            @ColumnInfo(name = "large") val large: String
    )

    data class Coordinates(
            @ColumnInfo(name = "latitude") val latitude: Double,
            @ColumnInfo(name = "longitude") val longitude: Double
    )

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
    constructor(location: LocationsResponse.Location) :
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
