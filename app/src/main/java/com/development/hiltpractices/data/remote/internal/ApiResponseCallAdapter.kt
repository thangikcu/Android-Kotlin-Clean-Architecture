package com.development.hiltpractices.data.remote.internal

import com.development.hiltpractices.data.remote.ApiResponse
import kotlinx.coroutines.CoroutineScope
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

internal class ApiResponseCallAdapter constructor(
    private val resultType: Type,
    private val coroutineScope: CoroutineScope
) : CallAdapter<Type, Call<ApiResponse<Type>>> {

    override fun responseType(): Type {
        return resultType
    }

    override fun adapt(call: Call<Type>): Call<ApiResponse<Type>> {
        return ApiResponseCallDelegate(call, coroutineScope)
    }
}
