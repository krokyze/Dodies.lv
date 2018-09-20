package com.krokyze.dodies.di

import android.arch.persistence.room.Room
import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.krokyze.dodies.BuildConfig
import com.krokyze.dodies.repository.LocationRepository
import com.krokyze.dodies.repository.api.LocationApi
import com.krokyze.dodies.repository.db.AppDatabase
import com.krokyze.dodies.repository.db.LocationDao
import com.krokyze.dodies.repository.db.Migrations
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single { createOkHttpClient() }
    single { createLocationApi(get()) }
    single { createLocationDao(get()) }
    single { createLocationRepository(get(), get(), androidApplication()) }
}

private fun createOkHttpClient(): OkHttpClient {
    val clientBuilder = OkHttpClient.Builder()

    if (BuildConfig.DEBUG) {
        clientBuilder.addNetworkInterceptor(StethoInterceptor())
    }
    return clientBuilder.build()
}

private fun createLocationApi(okHttpClient: OkHttpClient): LocationApi {
    val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl("https://dodies.lv/")
            .build()

    return retrofit.create(LocationApi::class.java)
}

private fun createLocationDao(context: Context): LocationDao {
    val appDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "app-database")
            .addMigrations(Migrations.Migration_1_2)
            .build()
    return appDatabase.locationDao()
}

private fun createLocationRepository(locationApi: LocationApi, locationDao: LocationDao, context: Context): LocationRepository {
    return LocationRepository(locationApi, locationDao, context.assets)
}
