package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class PersonBase(
    @SerializedName("id") val id: Int,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
)