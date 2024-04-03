package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName


data class RankingInfo(
    @SerializedName("rank") val rank: Int,
    @SerializedName("previous_rank") val previousRank: Int? = null,
)