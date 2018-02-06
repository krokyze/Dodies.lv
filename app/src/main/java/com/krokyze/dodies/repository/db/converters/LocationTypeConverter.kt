package com.krokyze.dodies.repository.db.converters

import android.arch.persistence.room.TypeConverter
import com.krokyze.dodies.repository.data.Location

/**
 * Created by krokyze on 05/02/2018.
 */
class LocationTypeConverter {
    @TypeConverter
    fun fromString(type: String) = Location.Type.fromString(type)

    @TypeConverter
    fun toString(type: Location.Type) = type.value
}