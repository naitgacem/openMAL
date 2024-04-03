package com.aitgacem.openmal.di

import android.content.Context
import com.aitgacem.openmal.R
import com.aitgacem.openmal.data.UserPreferencesRepository
import com.aitgacem.openmalnet.api.mal.AuthHandler
import com.aitgacem.openmalnet.data.refreshToken
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object ApiKey {
    @Provides
    @Named("api_key")
    fun provideApiKey(@ApplicationContext ctx: Context): String {
        return ctx.resources.getString(R.string.api_key)
    }

    @Provides
    @Named("access_token")
    fun provideAccessToken(
        userPreferencesRepository: UserPreferencesRepository,
    ): String? {
        return runBlocking {
            userPreferencesRepository.accessTokenFlow.firstOrNull()
        }
    }

    @Provides
    fun provideAccessTokenFLow(
        userPreferencesRepository: UserPreferencesRepository,
    ): Flow<String> {
        return userPreferencesRepository.accessTokenFlow
    }

    @Provides
    fun provideAuthHandler(
        prefs: UserPreferencesRepository,
        @Named("api_key") apiKey: String,
    ): AuthHandler = AuthHandlerImpl(prefs, apiKey)


}

class AuthHandlerImpl @Inject constructor(
    private val prefs: UserPreferencesRepository,
    private val apikey: String,
    private var retries: Int = 0,
    private var isLogged: Boolean = false,
) : AuthHandler {
    override fun shouldRetry(): Boolean {
        isLogged = runBlocking { prefs.isLoggedInFlow.firstOrNull() == true }
        if (!isLogged) return false
        if (retries < 3) {
            runBlocking { delay(100) }
            retries += 1
            return true
        }
        if (retries < 5) {
            runBlocking {
                val refreshToken = prefs.refreshTokenFlow.firstOrNull()
                val response = refreshToken(apikey, "refresh_token", refreshToken)
                prefs.updateAccessToken(response.token)
                prefs.updateRefreshToken(response.refreshToken)
                prefs.updateLoginStatus(true)
                delay(200)
                retries += 1
            }
            return true
        }
        if (retries < 15) {
            runBlocking { delay(300) }
            retries += 1
            return true
        }
        // Give up
        runBlocking { prefs.updateLoginStatus(false) }
        return false
    }

    override val accessToken: String
        get() = runBlocking {
            prefs.accessTokenFlow.firstOrNull() ?: ""
        }
}


