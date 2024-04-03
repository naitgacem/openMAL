package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class WorkBase(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("main_picture") val mainPicture: WorkBaseMainPicture? = null,
)