package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class AnimeForListAllOfMyListStatus(
    @SerializedName("status") val status: String? = null,
    @SerializedName("score") val score: Int,
    @SerializedName("num_episodes_watched") val numEpisodesWatched: Int,
    @SerializedName("is_rewatching") val isRewatching: Boolean,
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("finish_date") val finishDate: String? = null,
    @SerializedName("priority") val priority: Int,
    @SerializedName("num_times_rewatched") val numTimesRewatched: Int,
    @SerializedName("rewatch_value") val rewatchValue: Int,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("comments") val comments: String?,
    @SerializedName("updated_at") val updatedAt: Date,
)