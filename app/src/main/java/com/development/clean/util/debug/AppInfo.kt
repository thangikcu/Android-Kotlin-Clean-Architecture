@file:Suppress("MemberVisibilityCanBePrivate")

package com.development.clean.util.debug

import android.annotation.SuppressLint
import android.content.pm.PackageInfo
import android.os.Build
import android.provider.Settings
import androidx.core.content.pm.PackageInfoCompat
import com.development.clean.App
import com.development.clean.BuildConfig
import com.development.clean.data.local.sharedprefs.AppSharedPrefs
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
            @SuppressLint("HardwareIds")
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
            "v.${packageInfo.versionName}-${PackageInfoCompat.getLongVersionCode(packageInfo)}"
        OS_VERSION = Build.VERSION.RELEASE
        MODEL = Build.MODEL
    }
}