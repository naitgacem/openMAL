package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class ForumBoard(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("subboards") val subboards: List<ForumSubboard>? = null,
)