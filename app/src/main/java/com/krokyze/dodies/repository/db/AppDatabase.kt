package com.krokyze.dodies.repository.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.krokyze.dodies.repository.data.Location
import com.krokyze.dodies.repository.db.converters.LocationStatusConverter
import com.krokyze.dodies.repository.db.converters.LocationTypeConverter

/**
 * Created by krokyze on 05/02/2018.
 */
@Database(entities = [(Location::class)], version = 1)
@TypeConverters(LocationTypeConverter::class, LocationStatusConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
}