package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class AnimeForDetailsAllOfStatistics(
    @SerializedName("num_list_users") val numListUsers: Int,
    @SerializedName("status") private val status: AnimeStatisticsStatus,
)