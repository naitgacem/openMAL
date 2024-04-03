package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName


data class AnimeStatistics(
    @SerializedName("num_list_users") val numListUsers: Int? = null,
    @SerializedName("status") val status: AnimeStatisticsStatus? = null,
)
