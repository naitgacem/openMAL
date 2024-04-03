package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName
import java.net.URI


data class UserBase(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("picture") val picture: URI? = null,
)