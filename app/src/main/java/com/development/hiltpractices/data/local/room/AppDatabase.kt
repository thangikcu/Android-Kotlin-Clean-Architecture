package com.development.hiltpractices.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.development.hiltpractices.common.Constants
import com.development.hiltpractices.workers.SeedDatabaseWorker

@Database(entities = [Log::class], version = 1, exportSchema = false)
@TypeConverters(RoomConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun logDao(): LogDao

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