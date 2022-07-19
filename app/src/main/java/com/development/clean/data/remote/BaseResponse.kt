package com.development.clean.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class BaseResponse<T>(
    @Json(name = "data") val data: T,
    @Json(name = "errorCode") val errorCode: Int,
    @Json(name = "message") val message: String?
)