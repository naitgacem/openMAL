package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class MangaForDetails(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("main_picture") val mainPicture: WorkBaseMainPicture? = null,
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
    @SerializedName("created_at") val createdAt: Date,
    @SerializedName("updated_at") val updatedAt: Date,
    @SerializedName("media_type") val mediaType: String,
    @SerializedName("status") val status: String,
    @SerializedName("my_list_status") val myListStatus: MangaForListAllOfMyListStatus? = null,
    @SerializedName("num_volumes") val numVolumes: Int,
    @SerializedName("num_chapters") val numChapters: Int,
    @SerializedName("authors") val authors: List<PersonRoleEdge>,
    @SerializedName("pictures") val pictures: List<Picture>,
    @SerializedName("background") val background: String? = null,
    @SerializedName("related_anime") val relatedAnime: List<RelatedAnimeEdge>,
    @SerializedName("related_manga") val relatedManga: List<RelatedMangaEdge>,
    @SerializedName("recommendations") val recommendations: List<MangaRecommendationAggregationEdgeBase>,
    @SerializedName("serialization") val serialization: List<MangaMagazineRelationEdge>,
)