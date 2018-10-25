package com.krokyze.dodies

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.krokyze.dodies.repository.db.AppDatabase
import com.krokyze.dodies.repository.db.Migrations
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    @Rule
    @JvmField
    val helper: MigrationTestHelper = MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            AppDatabase::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory())

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        val db = helper.createDatabase(TEST_DB, 1)
        db.execSQL("INSERT OR REPLACE INTO `locations`(`id`, `favorite`,`url`,`name`,`type`,`text`,`status`,`distance`,`date`,`small`,`large`,`latitude`,`longitude`) VALUES ('id',1,'url','name','type','text','status','distance','date','small','large',56.06705,26.14555)")
        db.close()

        // Re-open the database with version 2 and provide MIGRATION_1_2 as the migration process.
        helper.runMigrationsAndValidate(TEST_DB, 2, true, Migrations.Migration_1_2)
    }

    companion object {
        const val TEST_DB = "migration-test"
    }
}
