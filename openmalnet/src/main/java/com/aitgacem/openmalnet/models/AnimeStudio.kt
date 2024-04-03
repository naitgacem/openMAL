package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName


data class AnimeStudio(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
)