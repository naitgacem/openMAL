
package com.aitgacem.openmalnet.models

import com.google.gson.annotations.SerializedName

data class UserMangaListEdge(
    @SerializedName("node") val node: MangaForList,
    @SerializedName("list_status") val listStatus: MangaListStatus? = null,
)