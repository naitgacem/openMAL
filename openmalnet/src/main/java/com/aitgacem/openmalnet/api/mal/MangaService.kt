package com.aitgacem.openmalnet.api.mal

import com.aitgacem.openmalnet.models.AnimeForDetails
import com.aitgacem.openmalnet.models.AnimeListForRanking
import com.aitgacem.openmalnet.models.AnimeListStatus
import com.aitgacem.openmalnet.models.MangaForDetails
import com.aitgacem.openmalnet.models.MangaListForRanking
import com.aitgacem.openmalnet.models.MangaListStatus
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface MangaService {
    @GET("manga/ranking")
    suspend fun getMangaRanking(
        @Query("ranking_type") rankingType: String,
        @Query("limit")limit: Int?,
        @Query("offset")offset: Int?,
        @Query("fields")fields: String?,
    ) : MangaListForRanking


    @GET("manga/{manga_id}")
    suspend fun getMangaDetails(
        @Path("manga_id") id: Int,
        @Query("fields") fields: String? = null,
    ): MangaForDetails
}