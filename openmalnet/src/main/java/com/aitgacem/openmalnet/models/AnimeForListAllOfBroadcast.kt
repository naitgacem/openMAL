package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName


data class AnimeForListAllOfBroadcast(
    @SerializedName("day_of_the_week") val dayOfTheWeek: String,
    @SerializedName("start_time") val startTime: String? = null,
)