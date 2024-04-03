package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName


data class RelatedMangaEdge(
    @SerializedName("node") val node: MangaForList,
    @SerializedName("relation_type") val relationType: String,
    @SerializedName("relation_type_formatted") val relationTypeFormatted: String,
)