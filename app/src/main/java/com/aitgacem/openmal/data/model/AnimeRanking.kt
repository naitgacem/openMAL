package com.aitgacem.openmal.data.model

enum class AnimeRanking(val title: String, val queryParam: String) {
    ALL("Top Anime Series", "all"),
    AIRING("Top Airing Anime", "airing"),
    UPCOMING("Top Upcoming Anime", "upcoming"),
    TV("Top Anime TV Series", "tv"),
    OVA("Top Anime OVA Series", "ova"),
    MOVIE("Top Anime Movies", "movie"),
    SPECIAL("Top Anime Specials", "special"),
    BY_POPULARITY("Top Anime by Popularity", "bypopularity"),
    FAVORITE("Top Favorited Anime", "favorite"),
    SUGGESTED("Suggested Anime", "suggested")
}


enum class MangaRanking(val title: String, val queryParam: String) {
    ALL("All", "all"),
    MANGA("Top Manga", "manga"),
    NOVELS("Top Novels", "novels"),
    ONESHOTS("Top One-shots", "oneshots"),
    DOUJIN("Top Doujinshi", "doujin"),
    MANHWA("Top Manhwa", "manhwa"),
    MANHUA("Top Manhua", "manhua"),
    BY_POPULARITY("Most Popular", "bypopularity"),
    FAVORITE("Most Favorited", "favorite")
}
