package com.development.hiltpractices.util.debug

import android.content.Context
import androidx.startup.Initializer
import com.development.hiltpractices.BuildConfig

@Suppress("unused")
class TimberInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("TimberInitializer is initialized.")
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
