package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName


data class RelatedAnimeEdge(
    @SerializedName("node") val node: AnimeForList,
    @SerializedName("relation_type") val relationType: String,
    @SerializedName("relation_type_formatted") val relationTypeFormatted: String,
)