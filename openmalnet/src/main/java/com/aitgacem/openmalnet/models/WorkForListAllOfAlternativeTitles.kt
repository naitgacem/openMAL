package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName


data class WorkForListAllOfAlternativeTitles(
    @SerializedName("synonyms") val synonyms: List<String>? = null,
    @SerializedName("en") val en: String? = null,
    @SerializedName("ja") val ja: String? = null,
)