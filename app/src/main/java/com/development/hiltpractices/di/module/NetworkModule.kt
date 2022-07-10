package com.development.hiltpractices.di.module

import com.development.hiltpractices.App
import com.development.hiltpractices.BuildConfig
import com.development.hiltpractices.common.Constants
import com.development.hiltpractices.data.remote.TokenRefreshAuthenticator
import com.development.hiltpractices.data.remote.api.AuthorizationService
import com.development.hiltpractices.util.debug.HttpLoggingInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.Duration
import javax.inject.Named
import javax.inject.Singleton

private const val UNSPLASH_RETROFIT = "UNSPLASH_RETROFIT"

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    private lateinit var unsplashRetrofit: Retrofit

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
            .connectTimeout(Duration.ofSeconds(Constants.NETWORK_TIMEOUT))
            .readTimeout(Duration.ofSeconds(Constants.NETWORK_TIMEOUT))
            .addInterceptor { chain ->
                val request = chain.request()

                val newUrl = request.url().newBuilder()
                    .addQueryParameter("language", "vi")
                    .build()

                val newRequest = request.newBuilder()
                    .url(newUrl)
                    .apply {
                        App.loginUser?.token?.let {
                            header("Authorization", "Bearer $it")
                        }
                        header("Authorization", "Client-ID ${BuildConfig.API_KEY}")
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
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClientBuilder.build())
            .build().also {
                unsplashRetrofit = it
            }
    }

}
