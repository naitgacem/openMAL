package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class Magazine(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
)
