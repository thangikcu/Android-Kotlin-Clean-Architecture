package com.development.hiltpractices.util.debug

import android.annotation.SuppressLint
import com.development.hiltpractices.App
import com.development.hiltpractices.BuildConfig
import com.development.hiltpractices.data.local.room.AppDatabase
import com.development.hiltpractices.data.local.room.Log
import com.development.hiltpractices.data.local.room.LogDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

object Logcat {
    private const val MAX_LOG_SIZE = 50

    val logs: ArrayList<LogInfo> = ArrayList()

    private lateinit var logDao: LogDao

    private lateinit var coroutineScope: CoroutineScope

    var onLogsUpdate: (() -> Unit)? = null

    init {
        if (BuildConfig.SHAKE_LOG) {
            logDao = AppDatabase.getDatabase(App.INSTANCE).logDao()

            coroutineScope = CoroutineScope(Dispatchers.IO)

            coroutineScope.launch {
                runCatching {
                    val log = logDao.getLog()

                    if (log?.logs?.isNotEmpty() == true) {
                        logs.addAll(
                            log.logs.onEach { logInfo ->
                                logInfo.loadFromCache = true
                            }
                        )
                        onLogsUpdate?.invoke()
                    }
                }
            }
        }
    }

    fun evenUpdateLog() {
        if (BuildConfig.SHAKE_LOG) {
            synchronized(this) {
                onLogsUpdate?.invoke()
                coroutineScope.launch {
                    runCatching {
                        logDao.insert(Log(logs))
                    }
                }
            }
        }
    }

    fun clearLog() {
        if (BuildConfig.SHAKE_LOG) {
            synchronized(this) {
                logs.clear()
                coroutineScope.launch {
                    runCatching {
                        logDao.deleteAll()
                    }
                }
            }
        }
    }

    private fun addLogInfo(logInfo: LogInfo) {
        if (BuildConfig.SHAKE_LOG) {
            if (logs.size > MAX_LOG_SIZE) {
                logs.removeAt(logs.size - 1)
            }
            logs.add(0, logInfo)
        }
    }

    fun getLogInfo(id: String): LogInfo? {
        if (BuildConfig.SHAKE_LOG) {
            for (i in logs.indices) {
                val logInfo = logs[i]
                if (logInfo.id == id) {
                    return logInfo
                }
            }
        }
        return null
    }

    @SuppressLint("SimpleDateFormat")
    fun createLogInfo(title: String, id: String = UUID.randomUUID().toString()): LogInfo {
        val logInfo = LogInfo(id)
        if (BuildConfig.SHAKE_LOG) {
            logInfo.run {
                val loginUser = "do not login"
/*            if (AppInstance.loginUser != null && AppInstance.loginUser.Profile != null) {
                loginUser = String.Format(
                    "{0} {1} ({2} - {3})",
                    AppInstance.loginUser?.Profile?.FirstName,
                    AppInstance.loginUser?.Profile?.LastName,
                    AppInstance.loginUser?.Profile?.MemberId,
                    AppInstance.loginUser?.Profile?.Email ??"null email");
            }*/
                this.title = title
                startTime = Calendar.getInstance().time
                addContent(title = "OS", content = AppInfo.OS_VERSION)
                addContent(title = "Device", content = AppInfo.MODEL)
                addContent(title = "Device Name", content = AppInfo.DEVICE_NAME)
                addContent(title = "Device Id", content = AppInfo.DEVICE_ID)
                addContent(title = "App version", content = AppInfo.VERSION)
                addContent(title = "Login User", content = loginUser)
                addContent(
                    title = "Time",
                    content = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sszzz").format(startTime)
                )
                addContent(title = "API", content = title)
                addLine()
                addLogInfo(this)
            }
        }

        return logInfo
    }
}