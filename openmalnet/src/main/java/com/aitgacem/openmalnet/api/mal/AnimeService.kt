package com.aitgacem.openmalnet.api.mal

import com.aitgacem.openmalnet.models.AnimeForDetails
import com.aitgacem.openmalnet.models.AnimeList
import com.aitgacem.openmalnet.models.AnimeListForRanking
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AnimeService {

    @GET("anime/ranking")
    suspend fun getAnimeRanking(
        @Query("ranking_type") rankingType: String,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?,
        @Query("fields") fields: String?,
    ): Response<AnimeListForRanking>

    @GET("anime/suggestions")
    suspend fun getAnimeSuggestions(
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?,
        @Query("fields") fields: String?,
    ): Response<AnimeList>

    @GET("anime/season/{year}/{season}")
    suspend fun getAnimeBySeason(
        @Path("year") year: Int,
        @Path("season") season: String,
        @Query("sort") sort: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null,
        @Query("fields") fields: String? = null,
    ): Response<AnimeList>

    @GET("anime/{anime_id}")
    suspend fun getAnimeDetails(
        @Path("anime_id") id: Int,
        @Query("fields") fields: String? = null,
    ): Response<AnimeForDetails>

    @GET("anime")
    suspend fun getAnimeList(
        @Query("q") q: String = "",
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null,
        @Query("fields") fields: String? = null,
        @Query("nsfw") nsfw: Boolean = false,
    ): Response<AnimeList>
}