package com.development.clean.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeyDao {

    @Query("SELECT * FROM remote_key WHERE id = :id")
    suspend fun getById(id: String): RemoteKey?

    @Query("SELECT * FROM remote_key LIMIT 1")
    suspend fun getOne(): RemoteKey?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: List<RemoteKey>)

    @Query("DELETE FROM remote_key")
    suspend fun deleteAll()
}