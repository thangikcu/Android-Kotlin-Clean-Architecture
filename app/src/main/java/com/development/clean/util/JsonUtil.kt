package com.development.clean.util

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.text.SimpleDateFormat
import java.util.*

object JsonUtil {

    @Volatile
    private var INSTANCE: Moshi? = null

    fun getMoshi(): Moshi {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Moshi.Builder()
                .add(CustomDateAdapter())
                .addLast(KotlinJsonAdapterFactory())
                .build().also { INSTANCE = it }
        }
    }

    fun <T> toJson(obj: T, type: Class<T>): String {
        return getMoshi().adapter(type).toJson(obj)
    }

    @OptIn(ExperimentalStdlibApi::class)
    inline fun <reified T> toJson(obj: T): String {
        return getMoshi().adapter<T>().toJson(obj)!!
    }

    fun <T> fromJson(json: String, type: Class<T>): T {
        return getMoshi().adapter(type).fromJson(json)!!
    }

    @OptIn(ExperimentalStdlibApi::class)
    inline fun <reified T> fromJson(json: String): T {
        return getMoshi().adapter<T>().fromJson(json)!!
    }

/*    inline fun <reified T> moshiAdapter(): JsonAdapter<T> {
        val moshi = Moshi.Builder().build()
        val mainType = typeOf<T>()
        val finalType = Types.newParameterizedType(
            T::class.java,
            *mainType.arguments.map { it.type!!.javaType }.toTypedArray()
        )
        return moshi.adapter(finalType)
    }*/
}

class CustomDateAdapter : JsonAdapter<Date>() {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

    @FromJson
    override fun fromJson(reader: JsonReader): Date? {
        return try {
            val dateAsString = reader.nextString()
            synchronized(dateFormat) {
                dateFormat.parse(dateAsString)
            }
        } catch (e: Exception) {
            null
        }
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: Date?) {
        if (value != null) {
            synchronized(dateFormat) {
                writer.value(dateFormat.format(value))
            }
        }
    }
}