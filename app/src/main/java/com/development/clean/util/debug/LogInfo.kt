package com.development.clean.util.debug

import android.annotation.SuppressLint
import androidx.annotation.Keep
import com.development.clean.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.text.SimpleDateFormat
import java.util.*
import org.json.JSONObject

@JsonClass(generateAdapter = true)
@Keep
data class LogInfo constructor(@Json(name = "id") var id: String) {
    @Json(name = "title")
    var title: String? = null

    @Json(name = "subTitle")
    var subTitle: String? = null

    @Json(name = "startTime")
    lateinit var startTime: Date

    @Json(name = "loadFromCache")
    var loadFromCache = false

    @Json(name = "contentSegment")
    val contentSegment: ArrayList<String> = ArrayList()

    fun getContent(): String {
        return if (contentSegment.isNotEmpty()) {
            val content = StringBuilder()
            for (c in contentSegment) {
                content.append(c)
            }
            subTitle + "\n" + content
        } else {
            "!!!nothing!!!"
        }
    }

    fun addContent(content: String? = null, title: String? = null): LogInfo {
        if (BuildConfig.SHAKE_LOG) {
            var formatContent: String? = content

            if (!formatContent.isNullOrEmpty()) {
                try {
                    formatContent = JSONObject(formatContent).toString(2)
                } catch (ignored: Exception) {
                }
            }
            contentSegment.add(
                if (title.isNullOrEmpty()) formatContent
                    ?: "" else "$title: ${formatContent ?: ""} \n"
            )
        }
        return this
    }

    fun addLine(): LogInfo {
        contentSegment.add("----------------------------------------\n")
        return this
    }

    @SuppressLint("SimpleDateFormat")
    fun commitLog(): LogInfo {
        val diffInSeconds = (Calendar.getInstance().time.time - startTime.time).toDouble() / 1000

        subTitle =
            SimpleDateFormat("dd/MM HH:mm:ss").format(startTime) + " ~($diffInSeconds seconds)"
        Logcat.evenUpdateLog()
        Timber.d(getContent())
        return this
    }
}