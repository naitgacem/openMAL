package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class UserAnimeListEdge(
    @SerializedName("node") val node: AnimeForList,
    @SerializedName("list_status") val listStatus: AnimeListStatus? = null,
)
