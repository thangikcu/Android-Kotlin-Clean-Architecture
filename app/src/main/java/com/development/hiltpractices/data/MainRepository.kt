package com.development.hiltpractices.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.development.hiltpractices.data.local.room.AppDatabase
import com.development.hiltpractices.data.remote.api.UnsplashService
import com.development.hiltpractices.feature.searchphoto.PhotoRemoteMediator
import com.development.hiltpractices.feature.searchphoto.SearchPhotoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val unsplashService: UnsplashService,
    private val appDatabase: AppDatabase,
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getPhotos(query: String): Flow<PagingData<SearchPhotoResponse.Photo>> {

        val pagingSource = { appDatabase.unsplashDao.queryBy() }

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = PhotoRemoteMediator(unsplashService, appDatabase, query),
            pagingSourceFactory = pagingSource
        ).flow.flowOn(Dispatchers.IO)
    }

    companion object {
        const val PAGE_SIZE = 20

    }
}