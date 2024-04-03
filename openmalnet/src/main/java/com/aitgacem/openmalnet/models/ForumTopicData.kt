package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class ForumTopicData(
    @SerializedName("title") var title: String? = null,
    @SerializedName("posts") val posts: List<ForumTopicPost>? = null,
    @SerializedName("poll") val poll: List<ForumTopicPoll>? = null,
)