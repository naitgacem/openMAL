package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class MangaListAllOfData(
    @SerializedName("node") val node: MangaForList
)