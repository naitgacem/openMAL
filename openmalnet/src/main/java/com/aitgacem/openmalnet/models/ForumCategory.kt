package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class ForumCategory(
    @SerializedName("title") val title: String? = null,
    @SerializedName("boards") val boards: List<ForumBoard>? = null,
)