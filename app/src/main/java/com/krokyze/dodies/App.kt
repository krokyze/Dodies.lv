package com.krokyze.dodies

import android.app.Application
import android.arch.persistence.room.Room
import android.support.annotation.NonNull
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.facebook.stetho.Stetho
import com.krokyze.dodies.repository.LocationRepository
import com.krokyze.dodies.repository.api.LocationApi
import com.krokyze.dodies.repository.db.AppDatabase
import io.fabric.sdk.android.Fabric
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import timber.log.Timber.DebugTree


/**
 * Created by krokyze on 05/02/2018.
 */
class App : Application() {

    companion object {
        lateinit var locationRepository: LocationRepository
    }

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        Stetho.initializeWithDefaults(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }

        val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://dodies.lv/")
                .build()

        val locationApi = retrofit.create(LocationApi::class.java)
        val appDatabase = Room.databaseBuilder(this, AppDatabase::class.java, "app-database")
                .fallbackToDestructiveMigration()
                .build()
        val locationDao = appDatabase.locationDao()

        locationRepository = LocationRepository(locationApi, locationDao, assets)
    }

    /** A tree which logs important information for crash reporting.  */
    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, @NonNull message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }

            Crashlytics.log(priority, tag, message)

            if (t != null) {
                if (priority == Log.ERROR || priority == Log.WARN) {
                    Crashlytics.logException(t)
                }
            }
        }
    }


}
