package com.development.hiltpractices.data.remote.api

import com.development.hiltpractices.data.remote.BaseResponse
import retrofit2.http.GET

interface AuthorizationService {

    @GET("/refreshToken")
    suspend fun refreshToken(): BaseResponse<String>
}