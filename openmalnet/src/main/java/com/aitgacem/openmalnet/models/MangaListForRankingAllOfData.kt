package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class MangaListForRankingAllOfData(
    @SerializedName("node") val node: MangaForList,
    @SerializedName("ranking") val ranking: RankingInfo? = null,
)
