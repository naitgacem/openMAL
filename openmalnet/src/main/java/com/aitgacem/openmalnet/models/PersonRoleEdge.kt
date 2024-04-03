package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName


data class PersonRoleEdge(
    @SerializedName("node") val node: PersonBase,
    @SerializedName("role") val role: String,

    )