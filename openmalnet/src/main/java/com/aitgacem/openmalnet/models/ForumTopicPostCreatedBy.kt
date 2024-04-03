package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName


data class ForumTopicPostCreatedBy(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("forum_avator") val forumAvator: String? = null,
)