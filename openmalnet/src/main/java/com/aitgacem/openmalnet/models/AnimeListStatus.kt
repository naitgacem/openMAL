package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class AnimeListStatus(
    @SerializedName("status") val status: String? = null,
    @SerializedName("score") val score: Int? = null,
    @SerializedName("num_episodes_watched") val numEpisodesWatched: Int? = null,
    @SerializedName("is_rewatching") val isRewatching: Boolean? = null,
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("finish_date") val finishDate: String? = null,
    @SerializedName("priority") val priority: Int? = null,
    @SerializedName("num_times_rewatched") val numTimesRewatched: Int? = null,
    @SerializedName("rewatch_value") val rewatchValue: Int? = null,
    @SerializedName("tags") val tags: List<String>? = null,
    @SerializedName("comments") val comments: String? = null,
    @SerializedName("updated_at") val updatedAt: Date? = null
)