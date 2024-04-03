package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class ForumTopicPoll(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("question") val question: String? = null,
    @SerializedName("close") val close: Boolean? = null,
    @SerializedName("options") val options: List<ForumTopicPollOption>? = null,
)