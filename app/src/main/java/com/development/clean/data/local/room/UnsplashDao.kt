package com.development.clean.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.development.clean.feature.searchphoto.SearchPhotoResponse

@Dao
interface UnsplashDao {

/*
    @Query(
        "SELECT * FROM Photo WHERE "
                + "tags LIKE :query "
                + "ORDER BY likes DESC"
    )
    fun queryBy(query: String): PagingSource<Int, SearchPhotoResponse.Photo>
*/

    @Query("SELECT * FROM Photo")
    fun queryBy(): PagingSource<Int, SearchPhotoResponse.Photo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: List<SearchPhotoResponse.Photo>)

    @Query("DELETE FROM Photo")
    suspend fun deleteAll()
}