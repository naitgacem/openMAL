package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ForumTopicPost(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("number") val number: Int? = null,
    @SerializedName("created_at") val createdAt: Date? = null,
    @SerializedName("created_by") val createdBy: ForumTopicPostCreatedBy? = null,
    @SerializedName("body") val body: String? = null,
    @SerializedName("signature") val signature: String? = null,
)