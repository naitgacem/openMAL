package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName


data class ForumSubboard(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("title") val title: String? = null,
)