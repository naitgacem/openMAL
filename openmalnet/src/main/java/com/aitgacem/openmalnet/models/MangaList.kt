package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class MangaList(
    @SerializedName("paging") val paging: MALListPaging? = null,
    @SerializedName("data") val data: List<MangaListAllOfData>? = null,
)