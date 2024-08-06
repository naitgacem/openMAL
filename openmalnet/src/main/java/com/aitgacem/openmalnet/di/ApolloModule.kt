package com.aitgacem.openmalnet.di

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.okHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import okhttp3.OkHttpClient
import javax.inject.Named


@Module
@InstallIn(ActivityRetainedComponent::class)
object ApolloModule {
    @Provides
    fun provideApollo(
        @Named("apollo") okHttpClient: OkHttpClient,
    ): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl("https://graphql.anilist.co")
            .okHttpClient(okHttpClient)
            .build()
    }
}
