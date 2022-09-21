package com.development.clean.data.remote.internal

import com.development.clean.data.remote.ApiResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

internal class ApiResponseCallDelegate<T>(
    proxy: Call<T>,
    private val coroutineScope: CoroutineScope
) : CallDelegate<T, ApiResponse<T>>(proxy) {

    override fun enqueueImpl(callback: Callback<ApiResponse<T>>) {
        coroutineScope.launch {
            try {
                val response = proxy.awaitResponse()
                val apiResponse = ApiResponse.of { response }
                callback.onResponse(this@ApiResponseCallDelegate, Response.success(apiResponse))
            } catch (e: Exception) {
                callback.onResponse(
                    this@ApiResponseCallDelegate, Response.success(
                        ApiResponse.error(
                            e
                        )
                    )
                )
            }
        }
    }

    override fun executeImpl(): Response<ApiResponse<T>> = runBlocking {
        withContext(coroutineScope.coroutineContext) {
            val response = proxy.execute()
            val apiResponse = ApiResponse.of { response }
            Response.success(apiResponse)
        }
    }

    override fun cloneImpl() = ApiResponseCallDelegate(proxy.clone(), coroutineScope)
}
