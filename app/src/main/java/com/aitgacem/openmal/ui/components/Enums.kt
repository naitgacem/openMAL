package com.aitgacem.openmal.ui.components

enum class WatchingStatus(val displayName: String) {
    plan_to_watch("Plan to watch"),
    watching("Watching"),
    completed("Completed"),
    on_hold("On hold"),
    dropped("Dropped"),
}

enum class ReadingStatus(val displayName: String) {
    plan_to_read("Plan to read"),
    reading("Currently reading"),
    completed("Completed reading"),
    on_hold("On hold"),
    dropped("Dropped"),
}
enum class AnimeSortType(val displayName: String, val queryParam: String){
    LIST_SCORE("Score (Descending)", "list_score"),
    LIST_UPDATED("Updated At (Descending)", "list_updated_at"),
    ANIME_TITLE("Title (Ascending)", "anime_title"),
    ANIME_START_DATE("Start Date", "anime_start_date")
}
enum class MangaSortType(val displayName: String, val queryParam: String){
    LIST_SCORE("Score (Descending)", "list_score"),
    LIST_UPDATED_AT("Updated At (Descending)", "list_updated_at"),
    MANGA_TITLE("Title (Ascending)", "manga_title"),
    MANGA_START_DATE("Start Date", "manga_start_date")
}
