package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName


data class MangaMagazineRelationEdge(
    @SerializedName("node") val node: Magazine,
    @SerializedName("role") val role: String,
)
