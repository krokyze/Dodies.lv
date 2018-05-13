package com.krokyze.dodies.repository.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

object Migrations {
    val Migration_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create the new table
            database.execSQL("CREATE TABLE `locations_new` (`favorite` INTEGER NOT NULL, `url` TEXT NOT NULL, `name` TEXT NOT NULL, `type` TEXT NOT NULL, `text` TEXT NOT NULL, `status` TEXT NOT NULL, `distance` TEXT NOT NULL, `date` TEXT NOT NULL, `small` TEXT NOT NULL, `large` TEXT NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, PRIMARY KEY(`url`))")
            // Copy the data
            database.execSQL("INSERT INTO `locations_new` (`favorite`, `url`, `name`, `type`, `text`, `status`, `distance`, `date`, `small`, `large`, `latitude`, `longitude`) SELECT `favorite`, `url`, `name`, `type`, `text`, `status`, `distance`, `date`, `small`, `large`, `latitude`, `longitude` FROM `locations`")
            // Remove the old table
            database.execSQL("DROP TABLE `locations`")
            // Change the table name to the correct one
            database.execSQL("ALTER TABLE `locations_new` RENAME TO `locations`")
        }
    }
}