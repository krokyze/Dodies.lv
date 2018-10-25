package com.krokyze.dodies.repository.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.krokyze.dodies.repository.data.Location
import com.krokyze.dodies.repository.db.converters.LocationStatusConverter
import com.krokyze.dodies.repository.db.converters.LocationTypeConverter

/**
 * Created by krokyze on 05/02/2018.
 */
@Database(entities = [(Location::class)], version = 2)
@TypeConverters(LocationTypeConverter::class, LocationStatusConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
}
