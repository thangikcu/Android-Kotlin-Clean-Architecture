@file:Suppress("unused", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST")

package com.development.clean.data.local.sharedprefs

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.development.clean.App
import com.development.clean.common.Constants
import com.development.clean.model.User
import com.development.clean.util.JsonUtil
import com.development.clean.util.debug.Timber

const val DEVICE_ID = "device_id"
const val FIRST_OPEN = "first_open"
const val LOGIN_USER = "login_user"
const val AES_IV = "aes_iv"

object AppSharedPrefs {

    private val mSharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        Constants.APP_SHARED_PREFERENCES_NAME,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        App.INSTANCE.applicationContext,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

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

    inline fun <reified T> put(key: String, data: T) {
        put(key, data, T::class.java)
    }

    fun <T> put(key: String, data: T, type: Class<T>) {
        with(mSharedPreferences.edit()) {
            when (data) {
                is String? -> putString(key, data)
                is Boolean -> putBoolean(key, data)
                is Float -> putFloat(key, data)
                is Int -> putInt(key, data)
                is Long -> putLong(key, data)
                else -> putString(key, JsonUtil.toJson(data, type))
            }
            apply()
        }
    }

    inline fun <reified T> get(key: String, defaultValue: T): T {
        val kClass = T::class
        val type = when (kClass.simpleName?.lowercase()) {
            String::class.simpleName!!.lowercase() -> String::class.java
            Boolean::class.simpleName!!.lowercase() -> Boolean::class.java
            Float::class.simpleName!!.lowercase() -> Float::class.java
            Int::class.simpleName!!.lowercase() -> Int::class.java
            Long::class.simpleName!!.lowercase() -> Long::class.java
            else -> kClass.java
        }
        return get(key, defaultValue, type as Class<T>)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String, defaultValue: T, type: Class<T>): T {
        return with(mSharedPreferences) {
            when (type) {
                String::class.java -> getString(key, defaultValue as? String) as T
                Boolean::class.java -> getBoolean(key, defaultValue as Boolean) as T
                Float::class.java -> getFloat(key, defaultValue as Float) as T
                Int::class.java -> getInt(key, defaultValue as Int) as T
                Long::class.java -> getLong(key, defaultValue as Long) as T
                else -> {
                    val jsonString = getString(key, null)

                    if (jsonString.isNullOrEmpty()) {
                        defaultValue
                    } else {
                        try {
                            JsonUtil.fromJson(jsonString, type)
                        } catch (e: Exception) {
                            Timber.e(e)
                            defaultValue
                        }
                    }
                }
            }
        }
    }

    fun remove(key: String) {
        with(mSharedPreferences.edit()) {
            remove(key)
            apply()
        }
    }

    fun clear() {
        with(mSharedPreferences.edit()) {
            clear()
            apply()
        }
    }
}
