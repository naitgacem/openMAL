package openmal.domain

import androidx.annotation.Keep

@Keep
enum class MediaType { ANIME, MANGA, }

enum class ListStatus {
    IN_PROGRESS,
    COMPLETED,
    ON_HOLD,
    DROPPED,
    PLAN_TO,
    NON_EXISTENT,
}

enum class SortType {
    SCORE,
    LAST_UPDATE,
    TITLE,
    START_DATE,
    DEFAULT,
}

enum class Priority { LOW, MEDIUM, HIGH, }

enum class ReleaseStatus {
    CURRENTLY_RELEASING,
    FINISHED,
    NOT_YET_RELEASED,
    OTHER,
    ON_HIATUS,
}

enum class Season { WINTER, SPRING, SUMMER, FALL, }

enum class ContentRating(val description: String) {
    G("G - All Ages"),
    PG("PG - Children"),
    PG_13("PG-13 - Teens 13 and Older"),
    R("R - 17+ (violence & profanity)"),
    R_PLUS("R+ - Profanity & Mild Nudity"),
    RX("Rx - Hentai"),
    UNKNOWN("Unknown")
}

enum class PreferredTitleStyle {
    PREFER_DEFAULT,
    PREFER_ENGLISH,
    PREFER_JAPANESE,
    PREFER_ROMAJI
}