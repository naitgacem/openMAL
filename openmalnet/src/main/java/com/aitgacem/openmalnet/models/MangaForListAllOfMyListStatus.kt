package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class MangaForListAllOfMyListStatus(
    @SerializedName("status") val status: String? = null,
    @SerializedName("score") val score: Int,
    @SerializedName("num_volumes_read") val numVolumesRead: Int,
    @SerializedName("num_chapters_read") val numChaptersRead: Int,
    @SerializedName("is_rereading") val isRereading: Boolean,
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("finish_date") val finishDate: String? = null,
    @SerializedName("priority") val priority: Int,
    @SerializedName("num_times_reread") val numTimesReread: Int,
    @SerializedName("reread_value") val rereadValue: Int,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("comments") val comments: String?,
    @SerializedName("updated_at") val updatedAt: Date,
)