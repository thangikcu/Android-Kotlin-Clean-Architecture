package com.development.clean.util.debug

import android.content.Context
import androidx.startup.Initializer
import com.development.clean.BuildConfig
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy

@Suppress("unused")
class LoggerInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        if (BuildConfig.DEBUG) {
            val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true) // (Optional) Whether to show thread info or not. Default true
                .methodCount(2) // (Optional) How many method line to show. Default 2
                .methodOffset(7) // (Optional) Hides internal method calls up to offset. Default 5
                .tag("APP_LOGGER") // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build()
            Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))

            Timber.plant(object : Timber.DebugTree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    Logger.log(priority, tag, message, t)
                }
            })
            Timber.d("App logger is initialized.")
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
