package com.development.hiltpractices.data.remote.internal

import com.development.hiltpractices.data.remote.ApiResponse
import com.development.hiltpractices.util.debug.Timber
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ApiResponseCallAdapterFactory private constructor(
    private val coroutineScope: CoroutineScope
) : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        when (getRawType(returnType)) {
            Call::class.java -> {
                val callType = getParameterUpperBound(0, returnType as ParameterizedType)
                val rawType = getRawType(callType)
                if (rawType != ApiResponse::class.java) {
                    return null
                }

                val resultType = getParameterUpperBound(0, callType as ParameterizedType)
                return ApiResponseCallAdapter(resultType, coroutineScope)
            }
            Deferred::class.java -> {
                val callType = getParameterUpperBound(0, returnType as ParameterizedType)
                val rawType = getRawType(callType)
                if (rawType != ApiResponse::class.java) {
                    return null
                }

                val resultType = getParameterUpperBound(0, callType as ParameterizedType)
                return ApiResponseDeferredCallAdapter<Any>(resultType, coroutineScope)
            }
            else -> return null
        }
    }

    companion object {
        @JvmStatic
        fun create(
            coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + CoroutineName("thangggggggggg") + CoroutineExceptionHandler { _, throwable ->
                Timber.e(throwable)
            })
        ): ApiResponseCallAdapterFactory = ApiResponseCallAdapterFactory(coroutineScope)
    }
}
