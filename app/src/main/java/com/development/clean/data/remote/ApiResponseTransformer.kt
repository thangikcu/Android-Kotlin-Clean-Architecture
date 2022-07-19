@file:Suppress("unused")

package com.development.clean.data.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf

fun <T> ApiResponse<T>.getOrNull(): T? {
    return when (this) {
        is ApiResponse.Success -> data
        is ApiResponse.Failure.Error -> null
        is ApiResponse.Failure.Exception -> null
    }
}

fun <T> ApiResponse<T>.getOrElse(defaultValue: T): T {
    return when (this) {
        is ApiResponse.Success -> data
        is ApiResponse.Failure.Error -> defaultValue
        is ApiResponse.Failure.Exception -> defaultValue
    }
}

inline fun <T> ApiResponse<T>.getOrElse(defaultValue: () -> T): T {
    return when (this) {
        is ApiResponse.Success -> data
        is ApiResponse.Failure.Error -> defaultValue()
        is ApiResponse.Failure.Exception -> defaultValue()
    }
}

fun <T> ApiResponse<T>.getOrThrow(): T {
    when (this) {
        is ApiResponse.Success -> return data
        is ApiResponse.Failure.Error -> throw RuntimeException(message())
        is ApiResponse.Failure.Exception -> throw exception
    }
}

@JvmSynthetic
inline fun <T> ApiResponse<T>.onSuccess(
    crossinline onResult: ApiResponse.Success<T>.() -> Unit
): ApiResponse<T> {
    if (this is ApiResponse.Success) {
        onResult(this)
    }
    return this
}

@JvmSynthetic
inline fun <T> ApiResponse<T>.onFailure(
    crossinline onResult: ApiResponse.Failure<T>.() -> Unit
): ApiResponse<T> {
    if (this is ApiResponse.Failure<T>) {
        onResult(this)
    }
    return this
}

@JvmSynthetic
inline fun <T> ApiResponse<T>.onError(
    crossinline onResult: ApiResponse.Failure.Error<T>.() -> Unit
): ApiResponse<T> {
    if (this is ApiResponse.Failure.Error) {
        onResult(this)
    }
    return this
}

@JvmSynthetic
inline fun <T> ApiResponse<T>.onException(
    crossinline onResult: ApiResponse.Failure.Exception<T>.() -> Unit
): ApiResponse<T> {
    if (this is ApiResponse.Failure.Exception) {
        onResult(this)
    }
    return this
}

@JvmSynthetic
inline fun <T> ApiResponse<T>.onProcedure(
    crossinline onSuccess: ApiResponse.Success<T>.() -> Unit,
    crossinline onError: ApiResponse.Failure.Error<T>.() -> Unit,
    crossinline onException: ApiResponse.Failure.Exception<T>.() -> Unit
): ApiResponse<T> = apply {
    this.onSuccess(onSuccess)
    this.onError(onError)
    this.onException(onException)
}

fun <T> ApiResponse.Failure<T>.message(): String {
    return when (this) {
        is ApiResponse.Failure.Error -> message()
        is ApiResponse.Failure.Exception -> message()
    }
}

fun <T> ApiResponse.Failure.Error<T>.message(): String = toString()

fun <T> ApiResponse.Failure.Exception<T>.message(): String = toString()

fun <T> ApiResponse<T>.toLiveData(): LiveData<T> {
    val liveData = MutableLiveData<T>()
    if (this is ApiResponse.Success) {
        liveData.postValue(data)
    }
    return liveData
}

@JvmSynthetic
inline fun <T, R> ApiResponse<T>.toLiveData(
    crossinline transformer: T.() -> R
): LiveData<R> {
    val liveData = MutableLiveData<R>()
    if (this is ApiResponse.Success) {
        liveData.postValue(data.transformer())
    }
    return liveData
}

@JvmSynthetic
fun <T> ApiResponse<T>.toFlow(): Flow<T> {
    return if (this is ApiResponse.Success) {
        flowOf(data)
    } else {
        emptyFlow()
    }
}

@JvmSynthetic
inline fun <T, R> ApiResponse<T>.toFlow(
    crossinline transformer: T.() -> R
): Flow<R> {
    return if (this is ApiResponse.Success) {
        flowOf(data.transformer())
    } else {
        emptyFlow()
    }
}
