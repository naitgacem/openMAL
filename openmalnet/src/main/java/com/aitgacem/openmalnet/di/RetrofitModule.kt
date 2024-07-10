package com.aitgacem.openmalnet.di

import com.aitgacem.openmalnet.api.mal.AuthHandler
import com.aitgacem.openmalnet.api.mal.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named

@Module
@InstallIn(ActivityRetainedComponent::class)
object RetrofitModule {
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        factory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.myanimelist.net/v2/")
            .client(okHttpClient)
            .addConverterFactory(factory)
            .build()
    }
    @Provides
    fun provideConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    fun provideInterceptor(
        @Named("api_key") apiKey: String,
        @Named("access_token") accessToken: String?,
    ): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("X-MAL-CLIENT-ID", apiKey)
            if(!accessToken.isNullOrEmpty()){
                request.addHeader("Authorization", "Bearer $accessToken")
            }
            chain.proceed(request.build())
        }
    }

    @Provides
    fun provideOkHttpClient(
        interceptor: Interceptor,
        authHandler: AuthHandler,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .authenticator(TokenAuthenticator(authHandler))
            .build()
    }

}
