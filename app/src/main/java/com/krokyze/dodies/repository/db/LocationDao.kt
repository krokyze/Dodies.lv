package com.krokyze.dodies.repository.db

import android.arch.persistence.room.*
import com.krokyze.dodies.repository.data.Location
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by krokyze on 05/02/2018.
 */

@Dao
interface LocationDao {
    @Query("SELECT * FROM locations")
    fun getLocations(): Single<List<Location>>

    @Query("SELECT * FROM locations WHERE name = :name")
    fun getLocation(name: String): Flowable<Location>

    @Query("SELECT * FROM locations WHERE favorite = 1")
    fun getFavoriteLocations(): Flowable<List<Location>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(locations: List<Location>)

    @Query("DELETE FROM locations")
    fun deleteAll()

    @Update
    fun update(location: Location)
}