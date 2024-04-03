package com.aitgacem.openmalnet.di
import com.aitgacem.openmalnet.api.mal.MangaService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import retrofit2.Retrofit

@Module
@InstallIn(ActivityRetainedComponent::class)
object mangaService {
    @Provides
    fun provideMangaService(retrofit: Retrofit) : MangaService{
        return retrofit.create(MangaService::class.java)
    }
}
