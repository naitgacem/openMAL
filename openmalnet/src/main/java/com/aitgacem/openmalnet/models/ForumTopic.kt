package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class ForumTopic(
    @SerializedName("data")  val data: List<ForumTopicData>? = null,
    @SerializedName("paging") val paging: ForumTopicPaging? = null,
)