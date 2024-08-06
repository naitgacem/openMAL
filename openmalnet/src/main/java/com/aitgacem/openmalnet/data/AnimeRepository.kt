package com.aitgacem.openmalnet.data

import com.aitgacem.openmalnet.api.anilist.CharacterService
import com.aitgacem.openmalnet.api.mal.AnimeService
import com.aitgacem.openmalnet.models.AnimeCharactersQuery
import com.aitgacem.openmalnet.models.AnimeForDetails
import com.aitgacem.openmalnet.models.AnimeList
import com.aitgacem.openmalnet.models.AnimeListForRanking
import com.aitgacem.openmalnet.models.CharacterDetailsQuery
import kotlinx.coroutines.flow.firstOrNull
import openmal.domain.Character
import openmal.domain.Gender
import openmal.domain.NetworkResult
import openmal.domain.PreferredTitleStyle
import openmal.domain.Work
import javax.inject.Inject

class AnimeRepository @Inject constructor(
    private val animeService: AnimeService,
    private val prefs: UserPreferencesRepository,
    private val characterService: CharacterService
) {
    private val listFields = "id,title,main_picture,alternative_titles"
    private val detailFields =
        "id,title,main_picture,alternative_titles,start_date,end_date,created_at,updated_at,synopsis,mean,rank,popularity,num_list_users,genres,media_type,status,my_list_status{updated_at,created_at,tags,comments,start_date,finish_date},num_episodes,start_season,broadcast,source,average_episode_duration,rating,studios,pictures,related_anime,related_manga,recommendations,statistics"

    suspend fun getTopAiringAnime(
    ): NetworkResult<List<Work>> {
        return getRankingAnime("airing")
    }

    suspend fun getAnimeCharacters(id: Int): NetworkResult<List<Character>> {
        val result: NetworkResult<AnimeCharactersQuery.Data> = handleGraphQlApi {
            characterService.getAnimeCharacters(id)
        }
        return when (result) {
            is NetworkResult.Error -> NetworkResult.Error(result.code, result.apiError)
            is NetworkResult.Exception -> NetworkResult.Exception(result.e)
            is NetworkResult.Success -> {
                return NetworkResult.Success(result.data.Media?.characters?.nodes?.map { node ->
                    Character(
                        id = node!!.id, name = node.name?.full!!, imageURL = node.image?.medium
                    )
                } ?: listOf())
            }
        }
    }

    suspend fun getAnimeCharacterDetails(id: Int): NetworkResult<Character> {
        val result: NetworkResult<CharacterDetailsQuery.Data> = handleGraphQlApi {
            characterService.getCharacterDetails(id)
        }
        return when (result) {
            is NetworkResult.Error -> NetworkResult.Error(result.code, result.apiError)
            is NetworkResult.Exception -> NetworkResult.Exception(result.e)
            is NetworkResult.Success -> {
                val character =
                    result.data.Character ?: return NetworkResult.Exception(RuntimeException())
                return NetworkResult.Success(
                    Character(
                        id = character.id,
                        name = character.name?.full ?: "",
                        imageURL = character.image?.medium,
                        description = character.description,
                        gender = when (character.gender) {
                            "Male" -> Gender.Male
                            "Female" -> Gender.Female
                            else -> Gender.Unknown
                        },
                        age = character.age
                    )
                )
            }
        }
    }

    suspend fun getTopTvAnime(
    ): NetworkResult<List<Work>> {
        return getRankingAnime("tv")
    }


    suspend fun getTopSpecialAnime(
    ): NetworkResult<List<Work>> {
        return getRankingAnime("special")
    }

    private suspend fun getRankingAnime(rankingType: String): NetworkResult<List<Work>> {
        val preferredTitleStyle =
            prefs.preferredTitleStyle.firstOrNull() ?: PreferredTitleStyle.PREFER_DEFAULT
        val result: NetworkResult<AnimeListForRanking> = handleApi {
            animeService.getAnimeRanking(
                rankingType = rankingType, limit = 50, offset = 0, fields = listFields
            )
        }
        return when (result) {
            is NetworkResult.Error -> NetworkResult.Error(result.code, result.apiError)
            is NetworkResult.Exception -> NetworkResult.Exception(result.e)
            is NetworkResult.Success -> {
                val animeList = result.data.data.map { it.node }
                NetworkResult.Success(animeList.map { it.toListWork(preferredTitleStyle) })
            }
        }
    }

    suspend fun getTopSuggestedAnime(
    ): NetworkResult<List<Work>> {
        val preferredTitleStyle =
            prefs.preferredTitleStyle.firstOrNull() ?: PreferredTitleStyle.PREFER_DEFAULT
        val result: NetworkResult<AnimeList> = handleApi {
            animeService.getAnimeSuggestions(
                limit = 50, offset = 0, fields = listFields
            )
        }
        return when (result) {
            is NetworkResult.Error -> NetworkResult.Error(result.code, result.apiError)
            is NetworkResult.Exception -> NetworkResult.Exception(result.e)
            is NetworkResult.Success -> {
                val animeList = result.data.data.map { it.node }
                NetworkResult.Success(animeList.map { it.toListWork(preferredTitleStyle) })
            }
        }
    }

    suspend fun getTopPopularAnime(
    ): NetworkResult<List<Work>> {
        return getRankingAnime("bypopularity")
    }

    suspend fun getTopUpcomingAnime(
    ): NetworkResult<List<Work>> {
        return getRankingAnime("upcoming")
    }

    suspend fun getSeasonAnime(
        year: Int,
        season: String,
    ): NetworkResult<List<Work>> {
        val preferredTitleStyle =
            prefs.preferredTitleStyle.firstOrNull() ?: PreferredTitleStyle.PREFER_DEFAULT
        val result: NetworkResult<AnimeList> = handleApi {
            animeService.getAnimeBySeason(
                year = year, season = season, offset = 0, limit = 50, fields = listFields
            )
        }
        return when (result) {
            is NetworkResult.Error -> NetworkResult.Error(result.code, result.apiError)
            is NetworkResult.Exception -> NetworkResult.Exception(result.e)
            is NetworkResult.Success -> {
                val animeList = result.data.data.map { it.node }
                NetworkResult.Success(animeList.map { it.toListWork(preferredTitleStyle) })
            }
        }
    }

    suspend fun getAnimeDetails(
        id: Int,
    ): NetworkResult<Work> {
        val preferredTitleStyle =
            prefs.preferredTitleStyle.firstOrNull() ?: PreferredTitleStyle.PREFER_DEFAULT
        val result: NetworkResult<AnimeForDetails> = handleApi {
            animeService.getAnimeDetails(
                id = id,
                fields = "id,title,main_picture,alternative_titles,start_date,end_date,created_at,updated_at,synopsis,mean,rank,popularity,num_list_users,genres,media_type,status,my_list_status{updated_at,created_at,tags,comments,start_date,finish_date},num_episodes,start_season,broadcast,source,average_episode_duration,rating,studios,pictures,related_anime,related_manga,recommendations,statistics"
            )
        }
        return when (result) {
            is NetworkResult.Error -> NetworkResult.Error(result.code, result.apiError)
            is NetworkResult.Exception -> NetworkResult.Exception(result.e)
            is NetworkResult.Success -> NetworkResult.Success(
                result.data.toDetailsWork(
                    preferredTitleStyle
                )
            )
        }
    }

}

