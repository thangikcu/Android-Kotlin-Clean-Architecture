package com.development.clean.data.remote.api

import com.development.clean.data.remote.BaseResponse
import retrofit2.http.GET

interface AuthorizationService {

    @GET("/refreshToken")
    suspend fun refreshToken(): BaseResponse<String>
}