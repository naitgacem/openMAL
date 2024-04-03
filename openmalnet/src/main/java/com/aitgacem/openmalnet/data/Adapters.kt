package com.aitgacem.openmalnet.data

import com.aitgacem.openmalnet.models.AnimeForDetails
import com.aitgacem.openmalnet.models.AnimeForList
import com.aitgacem.openmalnet.models.AnimeForListAllOfMyListStatus
import com.aitgacem.openmalnet.models.AnimeStatisticsStatus
import com.aitgacem.openmalnet.models.MangaForDetails
import com.aitgacem.openmalnet.models.MangaForList
import com.aitgacem.openmalnet.models.MangaForListAllOfMyListStatus
import com.aitgacem.openmalnet.models.User
import com.aitgacem.openmalnet.models.UserAllOfAnimeStatistics
import openmal.domain.Anime
import openmal.domain.AnimeStudio
import openmal.domain.ContentRating
import openmal.domain.Genre
import openmal.domain.ListStatus
import openmal.domain.Manga
import openmal.domain.MediaType
import openmal.domain.Priority
import openmal.domain.ReleaseStatus
import openmal.domain.Season
import openmal.domain.UserListStatus
import openmal.domain.Work

fun String.toReleaseStatus(): ReleaseStatus {
    return when (this) {
        "not_yet_aired", "not_yet_published" -> ReleaseStatus.NOT_YET_RELEASED
        "currently_airing", "currently_publishing" -> ReleaseStatus.CURRENTLY_RELEASING
        "finished_airing", "finished" -> ReleaseStatus.FINISHED
        "on_hiatus" -> ReleaseStatus.ON_HIATUS
        else -> ReleaseStatus.OTHER
    }
}

fun String.toListStatus(): ListStatus {
    return when (this) {
        "watching", "reading" -> ListStatus.IN_PROGRESS
        "completed" -> ListStatus.COMPLETED
        "dropped" -> ListStatus.DROPPED
        "on_hold" -> ListStatus.ON_HOLD
        "plan_to_watch", "plan_to_read" -> ListStatus.PLAN_TO
        else -> ListStatus.NON_EXISTENT
    }
}

fun AnimeForList.toListWork(): Work {
    this.let {
        val englishTitle = it.alternativeTitles?.en ?: ""
        return Anime(
            id = it.id,
            originalTitle = it.title,
            pictureURL = it.mainPicture?.medium.toString(),
            userPreferredTitle = englishTitle.ifBlank { it.title },
            synonyms = it.alternativeTitles?.synonyms ?: emptyList(),
            meanScore = it.mean
        )
    }
}

fun MangaForDetails.toDetailsWork(): Work {
    this.let { manga: MangaForDetails ->
        return Manga(
            id = manga.id,
            originalTitle = manga.title,
            pictureURL = manga.mainPicture?.medium.toString(),
            userPreferredTitle = manga.alternativeTitles?.let { alts ->
                if (alts.en.isNullOrEmpty()) manga.title else alts.en
            } ?: manga.title,
            synonyms = manga.alternativeTitles?.synonyms ?: emptyList(),
            contentType = manga.mediaType,
            releaseStatus = manga.status.toReleaseStatus(),
            numReleases = manga.numChapters,
            startDate = manga.startDate,
            endDate = manga.endDate,
            synopsis = manga.synopsis ?: "",
            meanScore = manga.mean,
            rank = manga.rank,
            popularity = manga.popularity,
            members = manga.numListUsers,
            nsfw = manga.nsfw != "white",
            genres = manga.genres.map { Genre(name = it.name) },
            createdAt = manga.createdAt,
            updatedAt = manga.updatedAt,
            listStatus = manga.myListStatus?.toUserList(),
            relatedWork = manga.relatedManga.map {
                Pair(it.node.toListWork(), it.relationTypeFormatted)
            },
            recommendations = manga.recommendations.map {
                Pair(it.node.toListWork(), it.numRecommendations)
            },
            numVolumes = manga.numVolumes,
            authors = List(manga.authors.size) { index ->
                val firstName = manga.authors[index].node.firstName
                val lastName = manga.authors[index].node.lastName
                val role = manga.authors[index].role
                Triple(firstName, lastName, role)
            }
        )
    }
}

fun AnimeForDetails.toDetailsWork(): Work {
    this.let { anime: AnimeForDetails ->
        return Anime(
            id = anime.id,
            originalTitle = anime.title,
            pictureURL = anime.mainPicture?.medium.toString(),
            userPreferredTitle = anime.alternativeTitles?.let { alts ->
                if (alts.en.isNullOrEmpty()) anime.title else alts.en
            } ?: anime.title,
            synonyms = anime.alternativeTitles?.synonyms ?: emptyList(),
            contentType = anime.mediaType,
            releaseStatus = anime.status.toReleaseStatus(),
            numReleases = anime.numEpisodes,
            startDate = anime.startDate,
            endDate = anime.endDate,
            synopsis = anime.synopsis ?: "",
            meanScore = anime.mean,
            rank = anime.rank,
            popularity = anime.popularity,
            members = anime.numListUsers,
            nsfw = anime.nsfw != "white",
            genres = anime.genres.map { Genre(name = it.name) },
            createdAt = anime.createdAt,
            updatedAt = anime.updatedAt,
            listStatus = anime.myListStatus?.toUserList(),
            relatedWork = anime.relatedAnime.map {
                Pair(it.node.toListWork(), it.relationTypeFormatted)
            },
            recommendations = anime.recommendations.map {
                Pair(it.node.toListWork(), it.numRecommendations)
            },
            startSeason = anime.startSeason?.let { Pair(it.season.toSeason(), it.year) },
            broadcastTime = anime.broadcast?.let { broadcast ->
                Pair(broadcast.dayOfTheWeek, broadcast.startTime ?: "?")
            } ?: Pair("unknown", "?"),

            source = anime.source ?: "",
            contentRating = anime.rating.toContentRating(),
            avgEpDuration = anime.averageEpisodeDuration ?: 0,
            studios = anime.studios.map { AnimeStudio(it.name) },
            statistics = anime.statistics?.status?.toStatistics() ?: emptyList(),
        )
    }
}

fun User.toUser(): openmal.domain.User {
    return openmal.domain.User(
        id = this.id,
        name = this.name,
        pictureURL = this.picture,
        gender = this.gender,
        birthday = this.birthday,
        location = this.location,
        joinDate = this.joinedAt,
        numItems = this.animeStatistics?.numItems ?: 0,
        numEpisodes = this.animeStatistics?.numEpisodes ?: 0,
        numDays = this.animeStatistics?.numDays ?: 0f,
        animeStats = this.animeStatistics.toAnimeStats(),
        dayStats = this.animeStatistics.toDayStats(),
        reWatched = this.animeStatistics?.numTimesRewatched ?: 0,
        meanScore = this.animeStatistics?.meanScore ?: 0f
    )
}

fun String?.toSeason(): Season {
    return when (this) {
        "winter" -> Season.WINTER
        "fall" -> Season.FALL
        "spring" -> Season.SPRING
        else -> Season.SUMMER
    }
}

fun UserAllOfAnimeStatistics?.toDayStats(): Map<ListStatus, Float> {
    val result = mutableMapOf<ListStatus, Float>()
    if (this == null){
        return result
    }
    result[ListStatus.DROPPED] = this.numDaysDropped
    result[ListStatus.ON_HOLD] = this.numDaysOnHold
    result[ListStatus.COMPLETED] = this.numDaysCompleted
    result[ListStatus.IN_PROGRESS] = this.numDaysWatching
    result[ListStatus.NON_EXISTENT] = this.numDays
    result[ListStatus.PLAN_TO] = 0f
    return result.toMap()
}
fun UserAllOfAnimeStatistics?.toAnimeStats(): Map<ListStatus, Int> {
    val result = mutableMapOf<ListStatus, Int>()
    if (this == null){
        return result
    }
    result[ListStatus.DROPPED] = this.numItemsDropped
    result[ListStatus.ON_HOLD] = this.numItemsOnHold
    result[ListStatus.PLAN_TO] = this.numItemsPlanToWatch
    result[ListStatus.COMPLETED] = this.numItemsCompleted
    result[ListStatus.IN_PROGRESS] = this.numItemsWatching
    result[ListStatus.NON_EXISTENT] = this.numItems
    return result.toMap()
}

fun String?.toContentRating(): ContentRating {
    return when (this) {
        "g" -> ContentRating.G
        "pg" -> ContentRating.PG
        "pg_13" -> ContentRating.PG_13
        "r" -> ContentRating.R
        "r+" -> ContentRating.R_PLUS
        "rx" -> ContentRating.RX
        else -> ContentRating.UNKNOWN
    }
}

fun AnimeStatisticsStatus.toStatistics(): List<Pair<ListStatus, Int>> {
    return listOf(
        ListStatus.IN_PROGRESS to this.watching,
        ListStatus.PLAN_TO to this.planToWatch,
        ListStatus.ON_HOLD to this.onHold,
        ListStatus.DROPPED to this.dropped,
        ListStatus.COMPLETED to this.completed
    )

}

fun MangaForListAllOfMyListStatus.toUserList(): UserListStatus {
    return UserListStatus(
        mediaType = MediaType.MANGA,
        currentStatus = this.status?.toListStatus() ?: ListStatus.NON_EXISTENT,
        score = this.score,
        progressCount = this.numChaptersRead,
        isRevisiting = this.isRereading,
        startDate = this.startDate ?: "",
        finishDate = this.finishDate ?: "",
        priority = when (this.priority) {
            0 -> Priority.LOW
            1 -> Priority.MEDIUM
            2 -> Priority.HIGH
            else -> Priority.LOW
        },
        numRevisited = this.numTimesReread,
        revisitValue = this.rereadValue,
        tags = this.tags,
        comments = this.comments ?: "",
        updatedAt = this.updatedAt,
    )
}

fun AnimeForListAllOfMyListStatus.toUserList(): UserListStatus {
    return UserListStatus(
        mediaType = MediaType.ANIME,
        currentStatus = this.status?.toListStatus() ?: ListStatus.NON_EXISTENT,
        score = this.score,
        progressCount = this.numEpisodesWatched,
        isRevisiting = this.isRewatching,
        startDate = this.startDate ?: "",
        finishDate = this.finishDate ?: "",
        priority = when (this.priority) {
            0 -> Priority.LOW
            1 -> Priority.MEDIUM
            2 -> Priority.HIGH
            else -> Priority.LOW
        },
        numRevisited = this.numTimesRewatched,
        revisitValue = this.rewatchValue,
        tags = this.tags,
        comments = this.comments ?: "",
        updatedAt = this.updatedAt,
    )
}

fun MangaForList.toListWork(): Work {
    this.let {
        val englishTitle = it.alternativeTitles?.en ?: ""
        return Manga(
            id = it.id,
            originalTitle = it.title,
            pictureURL = it.mainPicture?.medium.toString(),
            userPreferredTitle = englishTitle.ifBlank { it.title },
            synonyms = it.alternativeTitles?.synonyms ?: emptyList(),
            meanScore = it.mean,
        )
    }
}