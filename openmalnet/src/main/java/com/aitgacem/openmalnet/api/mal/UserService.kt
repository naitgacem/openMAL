package com.aitgacem.openmalnet.api.mal

import com.aitgacem.openmalnet.models.AnimeListStatus
import com.aitgacem.openmalnet.models.MangaListStatus
import com.aitgacem.openmalnet.models.User
import com.aitgacem.openmalnet.models.UserAnimeList
import com.aitgacem.openmalnet.models.UserMangaList
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {

    @FormUrlEncoded
    @PATCH("anime/{anime_id}/my_list_status")
    suspend fun updateAnimeLibStatus(
        @Path("anime_id") id: Int,
        @Field("status") status: String? = null,
        @Field("is_rewatching") isRewatching: Boolean? = null,
        @Field("score") score: Int? = null,
        @Field("num_watched_episodes") numWatchedEps: Int? = null,
        @Field("priority") priority: Int? = null,
        @Field("num_times_rewatched") numRewatched: Int? = null,
        @Field("rewatch_value") rewatchValue: Int? = null,
        @Field("tags") tags: String? = null,
        @Field("comments") comments: String? = null,
        @Field("start_date") startDate: String? = null,
        @Field("finish_date") finishDate: String? = null,
    ): Response<AnimeListStatus>

    @FormUrlEncoded
    @PATCH("manga/{manga_id}/my_list_status")
    suspend fun updateMangaLibStatus(
        @Path("manga_id") id: Int,
        @Field("status") status: String? = null,
        @Field("is_rereading") isRereading: Boolean? = null,
        @Field("score") score: Int? = null,
        @Field("num_volumes_read") numReadVols: Int? = null,
        @Field("num_chapters_read") numReadChaps: Int? = null,
        @Field("priority") priority: Int? = null,
        @Field("num_times_reread") numReread: Int? = null,
        @Field("reread_value") rereadValue: Int? = null,
        @Field("tags") tags: String? = null,
        @Field("comments") comments: String? = null,
        @Field("start_date") startDate: String? = null,
        @Field("finish_date") finishDate: String? = null,

        ): Response<MangaListStatus>

    @DELETE("anime/{anime_id}/my_list_status")
    suspend fun deleteAnimeFromList(
        @Path("anime_id") id: Int
    ): Response<Unit>

    @DELETE("manga/{manga_id}/my_list_status")
    suspend fun deleteMangaFromList(
        @Path("manga_id") id: Int
    ): Response<Unit>

    @GET("users/{user_name}/animelist")
    suspend fun getUserAnimeList(
        @Path("user_name") username: String = "@me",
        @Query("status") status: String? = null,
        @Query("sort") sort: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null,
        @Query("fields") fields: String?,
        @Query("nsfw") nsfw: Boolean = true
    ): Response<UserAnimeList>

    @GET("users/{user_name}/mangalist")
    suspend fun getUserMangaList(
        @Path("user_name") username: String = "@me",
        @Query("status") status: String? = null,
        @Query("sort") sort: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null,
        @Query("fields") fields: String?,
        @Query("nsfw") nsfw: Boolean = true
    ): Response<UserMangaList>

    @GET("users/{user_name}")
    suspend fun getMyUserInfo(
        @Path("user_name") username: String = "@me", // can only be "@me"
        @Query("fields") fields: String? = null,

        ): Response<User>
}