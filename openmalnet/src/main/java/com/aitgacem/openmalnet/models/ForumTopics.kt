package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class ForumTopics(
    @SerializedName("data") val data: List<ForumTopicsData>? = null,
    @SerializedName("paging") val paging: ForumTopicPaging? = null,
)