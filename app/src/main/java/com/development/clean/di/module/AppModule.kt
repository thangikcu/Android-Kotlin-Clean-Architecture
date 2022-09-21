package com.development.clean.di.module

import android.content.Context
import com.development.clean.data.local.room.AppDatabase
import com.development.clean.util.JsonUtil
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return JsonUtil.getMoshi()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext applicationContext: Context): AppDatabase {
        return AppDatabase.getDatabase(applicationContext)
    }
}