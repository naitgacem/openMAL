package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class MangaRecommendationAggregationEdgeBase(
    @SerializedName("node") val node: MangaForList,
    @SerializedName("num_recommendations") val numRecommendations: Int,
)