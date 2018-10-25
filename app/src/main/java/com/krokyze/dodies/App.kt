package com.krokyze.dodies

import android.app.Application
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.soloader.SoLoader
import com.facebook.stetho.Stetho
import com.krokyze.dodies.di.appModule
import com.krokyze.dodies.di.viewModule
import io.fabric.sdk.android.Fabric
import org.koin.android.ext.android.startKoin
import timber.log.Timber
import timber.log.Timber.DebugTree

/**
 * Created by krokyze on 05/02/2018.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(appModule, viewModule))

        Fabric.with(this, Crashlytics())
        Stetho.initializeWithDefaults(this)

        Fresco.initialize(this)
        SoLoader.init(this, false)

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
    }

    /** A tree which logs important information for crash reporting.  */
    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
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
