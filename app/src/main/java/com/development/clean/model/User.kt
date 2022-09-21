package com.development.clean.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "token")
    val token: String,
    @Json(name = "userName")
    val userName: String,
    @Json(name = "userEmail")
    val userEmail: String
)