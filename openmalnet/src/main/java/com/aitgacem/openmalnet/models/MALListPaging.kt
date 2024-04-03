package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class MALListPaging(
    @SerializedName("previous") val previous: String? = null,
    @SerializedName("next") val next: String? = null,
)
