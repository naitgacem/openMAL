package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName


data class ForumTopicPollOption(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("text") val text: String? = null,
    @SerializedName("votes") val votes: Int? = null,
)