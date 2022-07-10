package com.development.hiltpractices.data.local.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.development.hiltpractices.util.debug.LogInfo
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity
@JsonClass(generateAdapter = true)
data class Log(
    @ColumnInfo(name = "logs") @Json(name = "logs") val logs: List<LogInfo>,
    @PrimaryKey @Json(name = "id") val id: Int = 1
) {
}