package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName
import java.net.URI
import java.util.Date

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("picture") val picture: URI,
    @SerializedName("gender") val gender: String? = null,
    @SerializedName("birthday") val birthday: Date? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName("joined_at") val joinedAt: String,
    @SerializedName("anime_statistics") val animeStatistics: UserAllOfAnimeStatistics? = null,
    @SerializedName("time_zone") val timeZone: String? = null,
    @SerializedName("is_supporter") val isSupporter: Boolean? = null,
)