package com.development.clean.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.development.clean.common.Constants
import com.development.clean.feature.searchphoto.SearchPhotoResponse
import com.development.clean.util.debug.Log
import com.development.clean.workers.SeedDatabaseWorker

@Database(
    entities = [Log::class, RemoteKey::class, SearchPhotoResponse.Photo::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(RoomConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract val logDao: LogDao

    abstract val unsplashDao: UnsplashDao

    abstract val remoteKeyDao: RemoteKeyDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            Constants.APP_DATABASE_NAME
        )
            .addTypeConverter(RoomConverter())
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>()
                        .setInputData(workDataOf(SeedDatabaseWorker.KEY_FILENAME to Constants.LOG_DATA_FILENAME))
                        .build()
                    WorkManager.getInstance(context).enqueue(request)
                }
            })
            .build()
    }
}