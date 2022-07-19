package com.development.clean.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.development.clean.data.local.room.AppDatabase
import com.development.clean.data.remote.api.UnsplashService
import com.development.clean.feature.searchphoto.PhotoRemoteMediator
import com.development.clean.feature.searchphoto.SearchPhotoResponse
import com.development.clean.util.NetworkMonitor
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

@Singleton
class MainRepository @Inject constructor(
    private val unsplashService: UnsplashService,
    private val appDatabase: AppDatabase,
    private val networkMonitor: NetworkMonitor,
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
            remoteMediator = PhotoRemoteMediator(
                networkMonitor,
                unsplashService,
                appDatabase,
                query
            ),
            pagingSourceFactory = pagingSource
        ).flow.flowOn(Dispatchers.IO)
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}