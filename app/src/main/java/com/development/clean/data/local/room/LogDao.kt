package com.development.clean.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.development.clean.util.debug.Log

@Dao
interface LogDao {

    @Query("SELECT * FROM Log LIMIT 1")
    suspend fun getLog(): Log?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: Log)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(logs: List<Log>)

    @Query("DELETE FROM Log")
    suspend fun deleteAll()
}