package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class AnimeListForRankingAllOfData(
    @SerializedName("node")  val node: AnimeForList,
    @SerializedName("ranking") val ranking: RankingInfo? = null,
)

