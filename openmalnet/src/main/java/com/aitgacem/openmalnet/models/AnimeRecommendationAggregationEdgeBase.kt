package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class AnimeRecommendationAggregationEdgeBase(
    @SerializedName("node") val node: AnimeForList,
    @SerializedName("num_recommendations") val numRecommendations: Int,
)