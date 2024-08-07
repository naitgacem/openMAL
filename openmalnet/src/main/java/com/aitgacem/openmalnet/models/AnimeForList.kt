package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class AnimeForList(
    @SerializedName("id")  val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("main_picture")  val mainPicture: WorkBaseMainPicture? = null,
    @SerializedName("alternative_titles") val alternativeTitles: WorkForListAllOfAlternativeTitles? = null,
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("end_date") val endDate: String? = null,
    @SerializedName("synopsis") val synopsis: String? = null,
    @SerializedName("mean") val mean: Float? = null,
    @SerializedName("rank") val rank: Int? = null,
    @SerializedName("popularity") val popularity: Int? = null,
    @SerializedName("num_list_users") val numListUsers: Int,
    @SerializedName("num_scoring_users") val numScoringUsers: Int,
    @SerializedName("nsfw") val nsfw: String? = null,
    @SerializedName("genres") val genres: List<Genre>,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("media_type") val mediaType: String,
    @SerializedName("status") val status: String,
    @SerializedName("my_list_status") val myListStatus: AnimeForListAllOfMyListStatus? = null,
    @SerializedName("num_episodes") val numEpisodes: Int,
    @SerializedName("start_season") val startSeason: AnimeForListAllOfStartSeason? = null,
    @SerializedName("broadcast") val broadcast: AnimeForListAllOfBroadcast? = null,
    @SerializedName("source") val source: String? = null,
    @SerializedName("average_episode_duration") val averageEpisodeDuration: Int? = null,
    @SerializedName("rating") val rating: String? = null,
    @SerializedName("studios") val studios: List<AnimeStudio>,
)