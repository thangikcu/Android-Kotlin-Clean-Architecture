package com.development.hiltpractices.data.local.sharedprefs

import android.content.Context
import android.content.SharedPreferences
import com.development.hiltpractices.App
import com.development.hiltpractices.model.User
import com.development.hiltpractices.util.JsonUtil


const val DEVICE_ID = "device_id"
const val FIRST_OPEN = "first_open"
const val LOGIN_USER = "login_user"

object AppSharedPrefs {

    private val mSharedPreferences: SharedPreferences =
        App.INSTANCE.getSharedPreferences("App SharedPrefs", Context.MODE_PRIVATE)

    var deviceId: String? by GetSetPreferenceDelegate(
        DEVICE_ID,
        null,
        String::class.java
    )

    var firstOpen: Boolean by GetSetPreferenceDelegate(
        FIRST_OPEN,
        true,
        Boolean::class.java
    )

    var loginUser: User? by GetSetPreferenceDelegate(
        LOGIN_USER,
        null,
        User::class.java
    )


    fun <T> put(key: String, data: T, type: Class<T>) {
        mSharedPreferences.edit().apply {
            when (data) {
                is String -> putString(key, data as String)
                is Boolean -> putBoolean(key, data as Boolean)
                is Float -> putFloat(key, data as Float)
                is Int -> putInt(key, data as Int)
                is Long -> putLong(key, data as Long)
                else -> JsonUtil.toJson(data, type)
            }
            apply()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String, defaultValue: T, type: Class<T>): T {
        when (type) {
            String::class.java -> return mSharedPreferences.getString(
                key,
                if (defaultValue is String) defaultValue else (defaultValue as String?)
            ) as T
            Boolean::class.java -> return mSharedPreferences.getBoolean(
                key,
                defaultValue as Boolean
            ) as T
            Float::class.java -> return mSharedPreferences.getFloat(
                key,
                defaultValue as Float
            ) as T
            Int::class.java -> return mSharedPreferences.getInt(
                key,
                defaultValue as Int
            ) as T
            Long::class.java -> return mSharedPreferences.getLong(
                key,
                defaultValue as Long
            ) as T
            else -> {
                val jsonString = mSharedPreferences.getString(key, null)

                return if (jsonString.isNullOrEmpty()) {
                    defaultValue
                } else {
                    JsonUtil.fromJson(jsonString, type)
                }
            }
        }
    }

    fun remove(key: String) {
        mSharedPreferences.edit().apply {
            remove(key)
            apply()
        }
    }


}
