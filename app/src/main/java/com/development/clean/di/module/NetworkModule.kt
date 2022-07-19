package com.development.clean.di.module

import com.development.clean.App
import com.development.clean.BuildConfig
import com.development.clean.common.Constants
import com.development.clean.data.remote.TokenRefreshAuthenticator
import com.development.clean.data.remote.api.AuthorizationService
import com.development.clean.data.remote.api.UnsplashService
import com.development.clean.data.remote.internal.ApiResponseCallAdapterFactory
import com.development.clean.util.debug.HttpLoggingInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

private const val UNSPLASH_RETROFIT = "UNSPLASH_RETROFIT"

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    private lateinit var unsplashRetrofit: Retrofit

    @Provides
    @Singleton
    fun provideUnsplashService(@Named(UNSPLASH_RETROFIT) retrofit: Retrofit) =
        retrofit.create<UnsplashService>()

    @Provides
    @Singleton
    fun provideAuthorizationService(@Named(UNSPLASH_RETROFIT) retrofit: Retrofit): AuthorizationService {
        return retrofit.create(AuthorizationService::class.java)
    }

    @Provides
    @Singleton
    @Named(UNSPLASH_RETROFIT)
    fun provideUnsplashRetrofit(moshi: Moshi): Retrofit {
        val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
            .connectTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS)
            .addInterceptor { chain ->
                val request = chain.request()

                val newUrl = request.url.newBuilder()
//                    .addQueryParameter("language", "vi")
                    .build()

                val newRequest = request.newBuilder()
                    .url(newUrl)
                    .apply {
                        App.loginUser?.token?.let {
                            header("Authorization", "Bearer $it")
                        }
                        header("Authorization", "Client-ID ${BuildConfig.API_KEY}")
                        header("Cache-Control", "no-cache")
                    }
                    .build()
                chain.proceed(newRequest)
            }
            .authenticator(TokenRefreshAuthenticator { provideAuthorizationService(unsplashRetrofit) })

        if (BuildConfig.DEBUG) {
            okHttpClientBuilder.addInterceptor(HttpLoggingInterceptor())
        }

        return Retrofit.Builder()
            .baseUrl(BuildConfig.APP_BASE_URL)
            .client(okHttpClientBuilder.build())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build().also {
                unsplashRetrofit = it
            }
    }
}
