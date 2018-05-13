package com.krokyze.dodies

import android.app.Application
import android.arch.persistence.room.Room
import android.support.annotation.NonNull
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.soloader.SoLoader
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.krokyze.dodies.repository.LocationRepository
import com.krokyze.dodies.repository.api.LocationApi
import com.krokyze.dodies.repository.db.AppDatabase
import com.krokyze.dodies.repository.db.Migrations
import io.fabric.sdk.android.Fabric
import okhttp3.OkHttpClient
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

        Fresco.initialize(this)
        SoLoader.init(this, false)

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }

        val builder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(StethoInterceptor())
        }

        val retrofit = Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://dodies.lv/")
                .build()

        val locationApi = retrofit.create(LocationApi::class.java)
        val appDatabase = Room.databaseBuilder(this, AppDatabase::class.java, "app-database")
                .addMigrations(Migrations.Migration_1_2)
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
