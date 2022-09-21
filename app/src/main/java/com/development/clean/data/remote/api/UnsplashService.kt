package com.development.clean.data.remote.api

import com.development.clean.data.remote.ApiResponse
import com.development.clean.feature.searchphoto.SearchPhotoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashService {

    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): ApiResponse<SearchPhotoResponse>
}