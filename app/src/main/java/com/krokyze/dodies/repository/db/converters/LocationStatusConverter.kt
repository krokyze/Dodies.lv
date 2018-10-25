package com.krokyze.dodies.repository.db.converters

import androidx.room.TypeConverter
import com.krokyze.dodies.repository.data.Location

/**
 * Created by krokyze on 05/02/2018.
 */
class LocationStatusConverter {
    @TypeConverter
    fun fromString(status: String) = Location.Status.fromString(status)

    @TypeConverter
    fun toString(status: Location.Status) = status.value
}
