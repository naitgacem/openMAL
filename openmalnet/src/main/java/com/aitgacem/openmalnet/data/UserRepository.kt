package com.aitgacem.openmalnet.data

import com.aitgacem.openmalnet.api.mal.UserService
import com.aitgacem.openmalnet.models.AnimeListStatus
import com.aitgacem.openmalnet.models.MangaListStatus
import com.aitgacem.openmalnet.models.User
import com.aitgacem.openmalnet.models.UserAnimeList
import com.aitgacem.openmalnet.models.UserMangaList
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: UserService,
) {
    suspend fun getUserName(): String {
        return try {
            userService.getUserInfo(
                fields = "name"
            ).name ?: "Unknown"
        } catch (e: Exception) {
            e.printStackTrace()
            "Unknown error occurred"
        }
    }

    suspend fun updateAnimeListStatus(
        id: Int,
        status: String? = null,
        isRewatching: Boolean? = null,
        score: Int? = null,
        numWatchedEps: Int? = null,
        priority: Int? = null,
        numRewatched: Int? = null,
        rewatchValue: Int? = null,
        tags: String? = null,
        comments: String? = null,
        startDate: String? = null,
        finishDate: String? = null,
    ): AnimeListStatus? {
        return try {
            userService.updateAnimeLibStatus(
                id,
                status,
                isRewatching,
                score,
                numWatchedEps,
                priority,
                numRewatched,
                rewatchValue,
                tags,
                comments,
                startDate,
                finishDate
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updateMangaListStatus(
        id: Int,
        status: String? = null,
        isRereading: Boolean? = null,
        score: Int? = null,
        numReadVols: Int? = null,
        numReadChaps: Int? = null,
        priority: Int? = null,
        numReread: Int? = null,
        rereadValue: Int? = null,
        tags: String? = null,
        comments: String? = null,
        startDate: String? = null,
        finishDate: String? = null,
    ): MangaListStatus? {
        return try {
            userService.updateMangaLibStatus(
                id,
                status,
                isRereading,
                score,
                numReadVols,
                numReadChaps,
                priority,
                numReread,
                rereadValue,
                tags,
                comments,
                startDate,
                finishDate
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteAnimeFromList(
        id: Int
    ): Unit? {
        return try {
            userService.deleteAnimeFromList(id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteMangaFromList(
        id: Int
    ): Unit? {
        return try {
            userService.deleteMangaFromList(id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getUserAnimeList(
        username: String = "@me",
        status: String? = null,
        sort: String? = null,
        limit: Int? = null,
        offset: Int? = null,
    ): UserAnimeList? {
        return try {
            userService.getUserAnimeList(username, status, sort, limit, offset)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getUserMangaList(
        username: String = "@me",
        status: String? = null,
        sort: String? = null,
        limit: Int? = null,
        offset: Int? = null,
    ): UserMangaList? {
        return try {
            userService.getUserMangaList(username, status, sort, limit, offset)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getMyUserInfo(
        username: String = "@me",
        fields: String? = null,
    ): User? {
        return try {
            userService.getMyUserInfo(username, fields)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}