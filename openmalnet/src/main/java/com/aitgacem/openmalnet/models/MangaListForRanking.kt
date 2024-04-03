package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class MangaListForRanking(
    @SerializedName("paging") val paging: MALListPaging? = null,
    @SerializedName("data") val data: List<MangaListForRankingAllOfData>,
)
