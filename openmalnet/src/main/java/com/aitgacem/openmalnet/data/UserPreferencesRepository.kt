package com.aitgacem.openmalnet.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import openmal.domain.PreferredTitleStyle
import java.io.IOException
import javax.inject.Inject


class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    suspend fun updateLoginStatus(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = isLoggedIn
        }
    }

    val isLoggedInFlow: Flow<Boolean> = dataStore.data
        .catch { exception ->
            onError(exception)
        }
        .map { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] ?: false
        }

    val accessTokenFlow = dataStore.data.catch { exception ->
        onError(exception)
    }.map {
        it[PreferencesKeys.ACCESS_TOKEN] ?: ""
    }

    suspend fun updateAccessToken(token: String?) {
        runBlocking {
            dataStore.edit {
                if (token != null) {
                    it[PreferencesKeys.ACCESS_TOKEN] = token
                } else {
                    it.remove(PreferencesKeys.ACCESS_TOKEN)
                }
            }
        }
    }


    val refreshTokenFlow = dataStore.data.catch { exception ->
        onError(exception)
    }.map {
        it[PreferencesKeys.REFRESH_TOKEN] ?: ""
    }

    suspend fun updateRefreshToken(token: String?) {
        token?.let { refreshToken ->
            runBlocking {
                dataStore.edit {
                    it[PreferencesKeys.REFRESH_TOKEN] = refreshToken
                }
            }
        }
    }


    val codeChallenge: Flow<String> = dataStore.data.catch {
        onError(it)
    }.map {
        it[PreferencesKeys.CODE_CHALLENGE] ?: ""
    }

    suspend fun updateCodeChallenge(code: String) {
        dataStore.edit {
            it[PreferencesKeys.CODE_CHALLENGE] = code
        }
    }

    fun toggleNsfw(enableNsfw: Boolean) {
        runBlocking {
            dataStore.edit {
                it[PreferencesKeys.ENABLE_NSFW] = enableNsfw
            }
        }
    }

    val isNsfwEnabledFlow = dataStore.data.catch { exception ->
        onError(exception)
    }.map {
        it[PreferencesKeys.ENABLE_NSFW] ?: false
    }

    val preferredTitleStyle = dataStore.data.catch { exception ->
        onError(exception)
    }.map { prefs ->
        val rawString = prefs[PreferencesKeys.PREFERRED_TITLE_LANGUAGE]
        when (rawString) {
            PREFERRED_TITLE_LANGUAGE_ENGLISH -> PreferredTitleStyle.PREFER_ENGLISH
            PREFERRED_TITLE_LANGUAGE_JAPANESE -> PreferredTitleStyle.PREFER_JAPANESE
            PREFERRED_TITLE_LANGUAGE_ROMAJI -> PreferredTitleStyle.PREFER_ROMAJI

            else -> PreferredTitleStyle.PREFER_DEFAULT
        }
    }

    fun updatePreferredTitleStyle(style: PreferredTitleStyle) {
        runBlocking {
            dataStore.edit {
                it[PreferencesKeys.PREFERRED_TITLE_LANGUAGE] = when (style) {
                    PreferredTitleStyle.PREFER_DEFAULT -> "null"
                    PreferredTitleStyle.PREFER_ENGLISH -> PREFERRED_TITLE_LANGUAGE_ENGLISH
                    PreferredTitleStyle.PREFER_JAPANESE -> PREFERRED_TITLE_LANGUAGE_JAPANESE
                    PreferredTitleStyle.PREFER_ROMAJI -> PREFERRED_TITLE_LANGUAGE_ROMAJI
                }
            }
        }
    }
}

private const val PREFERRED_TITLE_LANGUAGE_JAPANESE = "prefer_japanese"
private const val PREFERRED_TITLE_LANGUAGE_ROMAJI = "prefer_romanji"
private const val PREFERRED_TITLE_LANGUAGE_ENGLISH = "prefer_english"

private object PreferencesKeys {
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    val CODE_CHALLENGE = stringPreferencesKey("code_challenge")
    val ENABLE_NSFW = booleanPreferencesKey("enable_nsfw")
    val PREFERRED_TITLE_LANGUAGE = stringPreferencesKey("preferred_title_language")
}

private suspend fun FlowCollector<Preferences>.onError(
    exception: Throwable
) {
    if (exception is IOException) {
        emit(emptyPreferences())
    } else {
        throw exception
    }
}