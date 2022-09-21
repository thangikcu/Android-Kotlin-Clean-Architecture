package com.development.clean.util.debug

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity
@JsonClass(generateAdapter = true)
@Keep
data class Log(
    @ColumnInfo(name = "logs") @Json(name = "logs") val logs: List<LogInfo>,
    @PrimaryKey @Json(name = "id") val id: Int = 1
)