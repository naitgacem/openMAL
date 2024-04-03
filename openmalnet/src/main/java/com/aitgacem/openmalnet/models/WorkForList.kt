package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class WorkForList(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("main_picture") private val mainPicture: WorkBaseMainPicture? = null,
    @SerializedName("alternative_titles") private val alternativeTitles: WorkForListAllOfAlternativeTitles? = null,
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("end_date") val endDate: String? = null,
    @SerializedName("synopsis") val synopsis: String? = null,
    @SerializedName("mean") val mean: Float? = null,
    @SerializedName("rank") val rank: Int? = null,
    @SerializedName("popularity") val popularity: Int? = null,
    @SerializedName("num_list_users") val numListUsers: Int? = null,
    @SerializedName("num_scoring_users") val numScoringUsers: Int? = null,
    @SerializedName("nsfw") val nsfw: String? = null,
    @SerializedName("genres") private val genres: List<Genre>? = null,
    @SerializedName("created_at") val createdAt: Date? = null,
    @SerializedName("updated_at") val updatedAt: Date? = null,
)