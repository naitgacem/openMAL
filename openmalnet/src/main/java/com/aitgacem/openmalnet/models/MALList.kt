package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName


data class MALList(
    @SerializedName("paging") val paging: MALListPaging? = null,
)
