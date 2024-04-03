package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class ForumBoards(
    @SerializedName("categories") val categories: List<ForumCategory>? = null,
)