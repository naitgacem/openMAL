package com.aitgacem.openmalnet.api.mal

import com.aitgacem.openmalnet.models.MangaForDetails
import com.aitgacem.openmalnet.models.MangaList
import com.aitgacem.openmalnet.models.MangaListForRanking
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MangaService {

    @GET("manga/ranking")
    suspend fun getMangaRanking(
        @Query("ranking_type") rankingType: String,
        @Query("limit")limit: Int?,
        @Query("offset")offset: Int?,
        @Query("fields")fields: String?,
        @Query("nsfw") nsfw: Boolean = false
    ) : Response<MangaListForRanking>

    @GET("manga/{manga_id}")
    suspend fun getMangaDetails(
        @Path("manga_id") id: Int,
        @Query("fields") fields: String? = null,
    ): Response<MangaForDetails>
    @GET("manga")
    suspend fun getMangaList(
        @Query("q") q: String = "",
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null,
        @Query("fields") fields: String? = null,
        @Query("nsfw") nsfw: Boolean = false
    ): Response<MangaList>
}