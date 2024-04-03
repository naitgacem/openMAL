package com.aitgacem.openmalnet.api.mal

import com.google.gson.annotations.SerializedName
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST
import javax.inject.Inject


interface AuthenticationService {
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("/v1/oauth2/token")
    suspend fun getToken(
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String,
        @Field("code") code: String,
        @Field("code_verifier") codeVerifier: String
    ): TokenResponse

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("/v1/oauth2/token")
    suspend fun refreshToken(
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String,
        @Field("refresh_token") code: String?,
    ): TokenResponse
}

data class TokenResponse(
    @SerializedName("token_type") val type: String,
    @SerializedName("expires_in") val expiration: Long,
    @SerializedName("access_token") val token: String,
    @SerializedName("refresh_token") val refreshToken: String,
)

class TokenAuthenticator @Inject constructor(
    private val authHandler: AuthHandler,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if(!authHandler.shouldRetry())
            return null

        val access = authHandler.accessToken

        if (access.isNotEmpty()) {
            return response.request.newBuilder()
                .header("Authorization", "Bearer $access")
                .build()
        }
        return null
    }
}

interface AuthHandler{
    fun shouldRetry() : Boolean
    val accessToken: String
}