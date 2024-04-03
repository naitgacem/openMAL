package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class AnimeList(
    @SerializedName("paging")  val paging: MALListPaging? = null,
    @SerializedName("data")  val data: List<AnimeListAllOfData>,
)
