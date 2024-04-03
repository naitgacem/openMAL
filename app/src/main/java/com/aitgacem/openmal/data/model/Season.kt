package com.aitgacem.openmal.data.model

enum class Season(val query: String) {
    SUMMER("summer"),
    WINTER("winter"),
    SPRING("spring"),
    FALL("fall"),
}

data class SeasonYear(
    val season: Season,
    val year: Int
)