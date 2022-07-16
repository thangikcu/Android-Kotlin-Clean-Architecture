package com.development.hiltpractices.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.development.hiltpractices.common.Constants
import com.development.hiltpractices.data.local.room.AppDatabase
import com.development.hiltpractices.util.JsonUtil
import com.development.hiltpractices.util.debug.Log
import com.development.hiltpractices.util.debug.Timber
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class SeedDatabaseWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val appDatabase: AppDatabase,
) : CoroutineWorker(context, workerParams) {
    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val filename = inputData.getString(KEY_FILENAME)
            if (filename != null) {
                applicationContext.assets.open(filename).use { inputStream ->
                    inputStream.bufferedReader().use {

                        if (filename == Constants.LOG_DATA_FILENAME) {

                            val log: List<Log> = JsonUtil.fromJson(it.readText())
                            appDatabase.logDao.insert(log)
                        }

                        Result.success()
                    }
                }
            } else {
                Timber.e("Error seeding database - no valid filename")
                Result.failure()
            }
        } catch (ex: Exception) {
            Timber.e("Error seeding database", ex)
            Result.failure()
        }
    }

    companion object {
        const val KEY_FILENAME = "KEY_FILENAME"
    }
}
