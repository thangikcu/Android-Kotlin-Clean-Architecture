@file:Suppress("unused")

package com.development.clean.util.extension

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_CALL
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes

val Context.isInternetAvailable: Boolean
    get() {
        val connectivityManager = getSystemService(ConnectivityManager::class.java)

        val activeNetwork = connectivityManager.activeNetwork ?: return false

        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

fun Context.browse(url: String, newTask: Boolean = false): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            if (newTask) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        true
    } catch (e: Exception) {
        false
    }
}

@SuppressLint("MissingPermission")
fun Context.makeCall(number: String): Boolean {
    return try {
        val intent = Intent(ACTION_CALL, Uri.parse("tel:$number"))
        startActivity(intent)
        true
    } catch (e: Exception) {
        false
    }
}

fun Context.rate(): Boolean =
    browse("market://details?id=$packageName") or browse("http://play.google.com/store/apps/details?id=$packageName")

val Context.screenWidth
    get() = resources.displayMetrics.widthPixels

val Context.screenHeight
    get() = resources.displayMetrics.heightPixels

val Context.isGpsEnabled: Boolean
    get() {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

fun Context.inflateLayout(
    @LayoutRes layoutId: Int,
    parent: ViewGroup? = null,
    attachToRoot: Boolean = false
): View = LayoutInflater.from(this).inflate(layoutId, parent, attachToRoot)

val Context.applicationInForeground: Boolean
    get() {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services = activityManager.runningAppProcesses
        var isActivityFound = false
        if (!services.isNullOrEmpty() && (services[0].processName.equals(
                packageName,
                ignoreCase = true
            )
                && services[0].importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
        ) {
            isActivityFound = true
        }
        return isActivityFound
    }

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}