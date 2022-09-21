package com.development.clean.data.local.sharedprefs

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class GetSetPreferenceDelegate<T>(
    private val key: String,
    private val defaultValue: T,
    private val type: Class<*>
) : ReadWriteProperty<AppSharedPrefs, T> {

    private var value: T? = null

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: AppSharedPrefs, property: KProperty<*>): T {
        return value ?: AppSharedPrefs.get(
            key,
            defaultValue,
            type as Class<T>
        ).also {
            value = it
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun setValue(thisRef: AppSharedPrefs, property: KProperty<*>, value: T) {
        this.value = value
        AppSharedPrefs.put(key, value, type as Class<T>)
    }
}