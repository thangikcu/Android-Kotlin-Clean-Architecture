package com.development.hiltpractices.di.module

import com.development.hiltpractices.App
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
class AppModule {
}