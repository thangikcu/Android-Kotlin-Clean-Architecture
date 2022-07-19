package com.development.clean.data.remote

import okhttp3.Headers
import okhttp3.ResponseBody
import retrofit2.Response

@Suppress("MemberVisibilityCanBePrivate", "unused")
sealed class ApiResponse<out T> {

    data class Success<T>(val response: Response<T>) : ApiResponse<T>() {
        val statusCode: StatusCode = getStatusCodeFromResponse(response)
        val headers: Headers = response.headers()
        val raw: okhttp3.Response = response.raw()
        val data: T by lazy { response.body() ?: throw NoContentException(statusCode.code) }
        override fun toString(): String = "[ApiResponse.Success](data=$data)"
    }

    sealed class Failure<T> : ApiResponse<T>() {

        data class Error<T>(val response: Response<T>) : Failure<T>() {
            val statusCode: StatusCode = getStatusCodeFromResponse(response)
            val headers: Headers = response.headers()
            val raw: okhttp3.Response = response.raw()
            val errorBody: ResponseBody? = response.errorBody()
            override fun toString(): String =
                "[ApiResponse.Failure.Error-$statusCode](errorResponse=$response)"
        }

        data class Exception<T>(val exception: Throwable) : Failure<T>() {
            val message: String? = exception.localizedMessage
            override fun toString(): String = "[ApiResponse.Failure.Exception](message=$message)"
        }
    }

    companion object {

        fun <T> error(ex: Throwable): Failure.Exception<T> =
            Failure.Exception(ex)

        @JvmSynthetic
        inline fun <T> of(
            crossinline f: () -> Response<T>
        ): ApiResponse<T> = try {
            val response = f()
            if (response.raw().code in 200..299) {
                Success(response)
            } else {
                Failure.Error(response)
            }
        } catch (ex: Exception) {
            Failure.Exception(ex)
        }

        fun <T> getStatusCodeFromResponse(response: Response<T>): StatusCode {
            return StatusCode.values().find { it.code == response.code() }
                ?: StatusCode.Unknown
        }
    }
}
