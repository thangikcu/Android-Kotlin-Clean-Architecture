package com.development.clean.feature.searchphoto

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.development.clean.common.exception.NetworkException
import com.development.clean.data.local.room.AppDatabase
import com.development.clean.data.local.room.RemoteKey
import com.development.clean.data.remote.api.UnsplashService
import com.development.clean.data.remote.getOrThrow
import com.development.clean.util.NetworkMonitor
import com.development.clean.util.debug.Timber
import java.util.concurrent.TimeUnit

private const val STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class PhotoRemoteMediator(
    private val networkMonitor: NetworkMonitor,
    private val unsplashService: UnsplashService,
    private val appDatabase: AppDatabase,
    private val query: String,
) : RemoteMediator<Int, SearchPhotoResponse.Photo>() {

    override suspend fun initialize(): InitializeAction {
        // Launch remote refresh as soon as paging starts and do not trigger remote prepend or
        // append until refresh has succeeded. In cases where we don't mind showing out-of-date,
        // cached offline data, we can return SKIP_INITIAL_REFRESH instead to prevent paging
        // triggering remote refresh.
        val cacheTimeout = TimeUnit.HOURS.toMillis(1)
        val time = appDatabase.remoteKeyDao.getOne()?.time

        return if (time != null && System.currentTimeMillis() - time >= cacheTimeout) {
            // Cached data is up-to-date, so there is no need to re-fetch
            // from the network.
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SearchPhotoResponse.Photo>,
    ): MediatorResult {

        if (!networkMonitor.isConnected) {
            return MediatorResult.Error(NetworkException())
        }

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for prepend.
                remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its nextKey is null, that means we've reached
                // the end of pagination for append.
                remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
        }

        try {
            val apiResponse =
                unsplashService.searchPhotos(query, page, state.config.pageSize).getOrThrow()

            val results = apiResponse.results
            val endOfPaginationReached = results.isEmpty()

            appDatabase.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    appDatabase.remoteKeyDao.deleteAll()
                    appDatabase.unsplashDao.deleteAll()
                }
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val time = System.currentTimeMillis()
                val keys = results.map {
                    RemoteKey(it.id, prevKey, nextKey, time)
                }
                appDatabase.remoteKeyDao.insert(keys)
                appDatabase.unsplashDao.insert(results)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            Timber.e("erorrrrrrrrr $e")
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, SearchPhotoResponse.Photo>): RemoteKey? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item

        return state.lastItemOrNull()?.let {
            // Get the remote keys of the last item retrieved
            appDatabase.remoteKeyDao.getById(it.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, SearchPhotoResponse.Photo>): RemoteKey? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.firstItemOrNull()?.let {
            // Get the remote keys of the first items retrieved
            appDatabase.remoteKeyDao.getById(it.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, SearchPhotoResponse.Photo>,
    ): RemoteKey? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let {
                appDatabase.remoteKeyDao.getById(it)
            }
        }
    }
}