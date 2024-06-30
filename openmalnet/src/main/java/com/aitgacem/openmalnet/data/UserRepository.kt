package com.aitgacem.openmalnet.data

import com.aitgacem.openmalnet.api.mal.UserService
import com.aitgacem.openmalnet.models.AnimeListStatus
import com.aitgacem.openmalnet.models.MangaListStatus
import kotlinx.coroutines.flow.firstOrNull
import openmal.domain.Anime
import openmal.domain.ListStatus
import openmal.domain.Manga
import openmal.domain.MediaType
import openmal.domain.NetworkResult
import openmal.domain.PreferredTitleStyle
import openmal.domain.SortType
import openmal.domain.User
import openmal.domain.UserListStatus
import openmal.domain.Work
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: UserService,
    private val prefs: UserPreferencesRepository,
) {


    suspend fun updateAnimeListStatus(
        id: Int,
        status: ListStatus,
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
    ): NetworkResult<AnimeListStatus> {
        val result: NetworkResult<AnimeListStatus> = handleApi {
            userService.updateAnimeLibStatus(
                id,
                when (status) {
                    ListStatus.IN_PROGRESS -> "watching"
                    ListStatus.COMPLETED -> "completed"
                    ListStatus.ON_HOLD -> "on_hold"
                    ListStatus.DROPPED -> "dropped"
                    ListStatus.PLAN_TO -> "plan_to_watch"
                    ListStatus.NON_EXISTENT -> null
                },
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
        }
        return when (result) {
            is NetworkResult.Error -> NetworkResult.Error(result.code, result.apiError)
            is NetworkResult.Exception -> NetworkResult.Exception(result.e)
            is NetworkResult.Success -> NetworkResult.Success(result.data)
        }
    }

    suspend fun updateMangaListStatus(
        id: Int,
        status: ListStatus,
        isRereading: Boolean? = null,
        score: Int? = null,
        numReadChaps: Int? = null,
        numReadVols: Int? = null,
        priority: Int? = null,
        numReread: Int? = null,
        rereadValue: Int? = null,
        tags: String? = null,
        comments: String? = null,
        startDate: String? = null,
        finishDate: String? = null,
    ): NetworkResult<MangaListStatus> {
        val result = handleApi {
            userService.updateMangaLibStatus(
                id,
                when (status) {
                    ListStatus.IN_PROGRESS -> "reading"
                    ListStatus.COMPLETED -> "completed"
                    ListStatus.ON_HOLD -> "on_hold"
                    ListStatus.DROPPED -> "dropped"
                    ListStatus.PLAN_TO -> "plan_to_read"
                    ListStatus.NON_EXISTENT -> null
                },
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
        }
        return when (result) {
            is NetworkResult.Error -> NetworkResult.Error(result.code, result.apiError)
            is NetworkResult.Exception -> NetworkResult.Exception(result.e)
            is NetworkResult.Success -> NetworkResult.Success(result.data)
        }
    }

    suspend fun deleteAnimeFromList(
        id: Int
    ): NetworkResult<Unit> {
        val result = handleApi {
            userService.deleteAnimeFromList(id)
        }
        return when (result) {
            is NetworkResult.Error -> NetworkResult.Error(result.code, result.apiError)
            is NetworkResult.Exception -> NetworkResult.Exception(result.e)
            is NetworkResult.Success -> NetworkResult.Success(result.data)
        }
    }

    suspend fun deleteMangaFromList(
        id: Int
    ): NetworkResult<Unit> {
        val result = handleApi {
            userService.deleteMangaFromList(id)
        }
        return when (result) {
            is NetworkResult.Error -> NetworkResult.Error(result.code, result.apiError)
            is NetworkResult.Exception -> NetworkResult.Exception(result.e)
            is NetworkResult.Success -> NetworkResult.Success(result.data)
        }
    }

    private val animeFields = "list_status,num_episodes,alternative_titles"
    private val mangaFields =  "list_status,num_chapters,alternative_titles"
    suspend fun getUserAnimeList(
        status: ListStatus = ListStatus.NON_EXISTENT,
        sort: SortType = SortType.DEFAULT,
    ): NetworkResult<List<Work>> {
        val nsfw = prefs.isNsfwEnabledFlow.firstOrNull() ?: false
        val result = handleApi {
            userService.getUserAnimeList(
                username = "@me",
                status = when (status) {
                    ListStatus.NON_EXISTENT -> null
                    ListStatus.DROPPED -> "dropped"
                    ListStatus.IN_PROGRESS -> "watching"
                    ListStatus.COMPLETED -> "completed"
                    ListStatus.ON_HOLD -> "on_hold"
                    ListStatus.PLAN_TO -> "plan_to_watch"
                },
                sort = when(sort){
                    SortType.SCORE -> "list_score"
                    SortType.LAST_UPDATE -> "list_updated_at"
                    SortType.TITLE -> "anime_title"
                    SortType.START_DATE -> "anime_start_date"
                    else -> null
                },
                limit = 50,
                offset = 0,
                nsfw = nsfw,
                fields = animeFields
            )

        }
        val preferredTitleStyle = prefs.preferredTitleStyle.firstOrNull() ?: PreferredTitleStyle.PREFER_DEFAULT
        return when (result) {
            is NetworkResult.Error -> NetworkResult.Error(result.code, result.apiError)
            is NetworkResult.Exception -> NetworkResult.Exception(result.e)
            is NetworkResult.Success -> {
                val animeList = result.data.data.map {
                    Anime(
                        id = it.node.id,
                        defaultTitle = it.node.title,
                        pictureURL = it.node.mainPicture?.medium.toString(),
                        userPreferredTitle = getAnimePreferredTitle(it.node, preferredTitleStyle),
                        synonyms = it.node.alternativeTitles?.synonyms ?: emptyList(),
                        numReleases = it.node.numEpisodes,
                        listStatus = UserListStatus( // TODO check the rest of the properties and how are they related to the [EditList]
                            mediaType = MediaType.ANIME,
                            currentStatus = it.listStatus?.status?.toListStatus()
                                ?: ListStatus.NON_EXISTENT,
                            progressCount = it.listStatus?.numEpisodesWatched ?: 0
                        )
                    )
                }
                NetworkResult.Success(animeList)
            }
        }
    }

    suspend fun getUserMangaList(
        status: ListStatus = ListStatus.NON_EXISTENT,
        sort: SortType = SortType.DEFAULT,
    ): NetworkResult<List<Work>> {
        val nsfw = prefs.isNsfwEnabledFlow.firstOrNull() ?: false
        val result = handleApi {
            userService.getUserMangaList(
                username = "@me",
                status = when (status) {
                    ListStatus.NON_EXISTENT -> null
                    ListStatus.DROPPED -> "dropped"
                    ListStatus.IN_PROGRESS -> "reading"
                    ListStatus.COMPLETED -> "completed"
                    ListStatus.ON_HOLD -> "on_hold"
                    ListStatus.PLAN_TO -> "plan_to_read"
                },
                sort =  when(sort){
                    SortType.SCORE -> "list_score"
                    SortType.LAST_UPDATE -> "list_updated_at"
                    SortType.TITLE -> "manga_title"
                    SortType.START_DATE -> "manga_start_date"
                    else -> null
                },
                limit = 50,
                offset = 0,
                nsfw = nsfw,
                fields = mangaFields
            )

        }
        val preferredTitleStyle = prefs.preferredTitleStyle.firstOrNull() ?: PreferredTitleStyle.PREFER_DEFAULT
        return when (result) {
            is NetworkResult.Error -> NetworkResult.Error(result.code, result.apiError)
            is NetworkResult.Exception -> NetworkResult.Exception(result.e)
            is NetworkResult.Success -> {
                val mangaList = result.data.data.map {
                    Manga(
                        id = it.node.id,
                        defaultTitle = it.node.title,
                        pictureURL = it.node.mainPicture?.medium.toString(),
                        userPreferredTitle = getMangaPreferredTitle(it.node, preferredTitleStyle),
                        synonyms = it.node.alternativeTitles?.synonyms ?: emptyList(),
                        numReleases = it.node.numChapters,
                        listStatus = UserListStatus( // TODO check the rest of the properties and how are they related to the [EditList]
                            mediaType = MediaType.MANGA,
                            currentStatus = it.listStatus?.status?.toListStatus()
                                ?: ListStatus.NON_EXISTENT,
                            progressCount = it.listStatus?.numChaptersRead ?: 0
                        )
                    )
                }
                NetworkResult.Success(mangaList)
            }
        }
    }


    suspend fun getMyUserInfo(
    ): NetworkResult<User> {
        val result = handleApi {
            userService.getMyUserInfo(fields = "anime_statistics")
        }
        return when (result) {
            is NetworkResult.Success -> NetworkResult.Success(result.data.toUser())
            is NetworkResult.Error -> NetworkResult.Error(result.code, result.apiError)
            is NetworkResult.Exception -> NetworkResult.Exception(result.e)
        }
    }
}