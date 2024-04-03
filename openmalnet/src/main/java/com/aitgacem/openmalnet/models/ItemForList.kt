package com.aitgacem.openmalnet.models

interface ItemForList {
    val id: Int
    val originalTitle: String
    val displayTitle: String
    val synonyms: List<String?>?
    val mainPicture: WorkBaseMainPicture?
    val type: String
}
