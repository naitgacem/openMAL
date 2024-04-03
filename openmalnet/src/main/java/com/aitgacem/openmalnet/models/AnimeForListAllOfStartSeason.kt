package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class AnimeForListAllOfStartSeason(
    @SerializedName("year") val year: Int,
    @SerializedName("season") val season: String ,
)