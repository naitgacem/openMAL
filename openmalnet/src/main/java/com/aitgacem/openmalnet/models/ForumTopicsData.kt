package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ForumTopicsData(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("created_at") val createdAt: Date? = null,
    @SerializedName("created_by") val createdBy: ForumTopicsCreatedBy? = null,
    @SerializedName("number_of_posts") val numberOfPosts: Int? = null,
    @SerializedName("last_post_created_at") val lastPostCreatedAt: Date? = null,
    @SerializedName("last_post_created_by") val lastPostCreatedBy: ForumTopicsCreatedBy? = null,
    @SerializedName("is_locked") val isLocked: Boolean? = null,
)