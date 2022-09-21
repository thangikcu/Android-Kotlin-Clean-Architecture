package com.development.clean.data.remote.internal

import com.development.clean.data.remote.ApiResponse
import java.lang.reflect.Type
import kotlinx.coroutines.CoroutineScope
import retrofit2.Call
import retrofit2.CallAdapter

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
