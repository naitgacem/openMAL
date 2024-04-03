package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class AnimeStatisticsStatus(
    @SerializedName("watching") val watching: Int,
    @SerializedName("completed") val completed: Int,
    @SerializedName("on_hold") val onHold: Int,
    @SerializedName("dropped") val dropped: Int,
    @SerializedName("plan_to_watch") val planToWatch: Int,
)