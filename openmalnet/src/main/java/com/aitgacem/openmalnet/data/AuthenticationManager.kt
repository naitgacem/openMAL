package com.aitgacem.openmalnet.data

import com.aitgacem.openmalnet.api.mal.AuthenticationService
import com.aitgacem.openmalnet.api.mal.TokenResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


suspend fun getToken(
    clientId: String,
    grantType: String,
    code: String,
    codeVerifier: String,
): TokenResponse {
    val http = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val ret = Retrofit.Builder().baseUrl("https://myanimelist.net/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(http)
        .build()

    val auth = ret.create(AuthenticationService::class.java)

    return auth.getToken(clientId, grantType, code, codeVerifier)
}


suspend fun refreshToken(
    clientId: String,
    grantType: String,
    refreshToken: String?,
): TokenResponse {
    val http = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val ret = Retrofit.Builder().baseUrl("https://myanimelist.net/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(http)
        .build()

    val auth = ret.create(AuthenticationService::class.java)

    return auth.refreshToken(clientId, grantType, refreshToken)
}
