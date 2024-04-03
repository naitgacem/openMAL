package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class AnimeListForRankingAllOfData(
    @SerializedName("node")  val node: AnimeForList? = null,
    @SerializedName("ranking") val ranking: RankingInfo? = null,
)

