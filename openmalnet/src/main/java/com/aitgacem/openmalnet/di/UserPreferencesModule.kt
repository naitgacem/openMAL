package com.aitgacem.openmalnet.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


private const val USER_PREFERENCES_NAME = "user_preferences"
private val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)


@Module
@InstallIn(SingletonComponent::class)
object DataStore {
    @Provides
    fun provideDataStore(
        @ApplicationContext ctx: Context
    ): DataStore<Preferences> {
        return ctx.dataStore
    }
}