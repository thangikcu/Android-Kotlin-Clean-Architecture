package com.development.clean

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.development.clean.data.local.sharedprefs.AppSharedPrefs
import com.development.clean.model.User
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        loginUser = AppSharedPrefs.loginUser
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .also {
                if (BuildConfig.DEBUG) {
                    it.setMinimumLoggingLevel(android.util.Log.DEBUG)
                }
            }
            .build()

    companion object {
        var INSTANCE: App by Delegates.notNull()

        var loginUser: User? = null
            set(value) {
                field = value
                AppSharedPrefs.loginUser = value
            }
    }
}