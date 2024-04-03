package com.aitgacem.openmalnet.di

import com.aitgacem.openmalnet.api.mal.AnimeService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import retrofit2.Retrofit

@Module
@InstallIn(ActivityRetainedComponent::class)
object animeService {
    @Provides
    fun provideAnimeService(retrofit: Retrofit) : AnimeService{
        return retrofit.create(AnimeService::class.java)
    }
}
