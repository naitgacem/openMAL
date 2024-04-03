package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName
import java.net.URI

data class WorkBaseMainPicture(
    @SerializedName("large") val large: URI? = null,
    @SerializedName("medium") val medium: URI,
)