package com.development.hiltpractices.util.debug

import android.content.Context
import android.content.pm.PackageInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import com.development.hiltpractices.App
import com.development.hiltpractices.BuildConfig
import com.development.hiltpractices.data.local.sharedprefs.AppSharedPrefs
import java.util.*
import kotlin.properties.Delegates

object AppInfo {

    var IS_PRODUCTION by Delegates.notNull<Boolean>()
    var DEVICE_NAME: String? = null
    var DEVICE_ID: String? = null
    var VERSION: String? = null
    var OS_VERSION: String? = null
    var MODEL: String? = null

    init {
        IS_PRODUCTION = BuildConfig.FLAVOR == "product"

        val context = App.INSTANCE.applicationContext

        val packageInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

        var deviceId: String? = AppSharedPrefs.deviceId

        if (deviceId.isNullOrEmpty()) {
            val androidId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )

            deviceId = androidId
            AppSharedPrefs.deviceId = deviceId
        }

        DEVICE_ID = deviceId
        DEVICE_NAME = "${Build.MANUFACTURER} ${Build.MODEL} (${Build.VERSION.SDK_INT})"

        VERSION =
            "v.${packageInfo.versionName}-${packageInfo.longVersionCode}"
        OS_VERSION = Build.VERSION.RELEASE
        MODEL = Build.MODEL
    }
}