package com.aitgacem.openmalnet.data


import com.aitgacem.openmalnet.api.mal.MangaService
import com.aitgacem.openmalnet.models.MangaForDetails
import com.aitgacem.openmalnet.models.MangaListForRanking
import kotlinx.coroutines.flow.firstOrNull
import openmal.domain.NetworkResult
import openmal.domain.PreferredTitleStyle
import openmal.domain.Work
import javax.inject.Inject

class MangaRepository @Inject constructor(
    private val mangaService: MangaService,
    private val prefs: UserPreferencesRepository,
) {
    private val listFields = "id,title,main_picture,alternative_titles"

    suspend fun getTopManga(
    ): NetworkResult<List<Work>> {
        return getRankingManga("manga")
    }

    suspend fun getTopNovels(
    ): NetworkResult<List<Work>> {
        return getRankingManga("novels")
    }

    private suspend fun getRankingManga(rankingType: String): NetworkResult<List<Work>> {
        val preferredTitleStyle = prefs.preferredTitleStyle.firstOrNull() ?: PreferredTitleStyle.PREFER_DEFAULT
        val nsfw = prefs.isNsfwEnabledFlow.firstOrNull() ?: false
        val result: NetworkResult<MangaListForRanking> = handleApi {
            mangaService.getMangaRanking(
                rankingType = rankingType, limit = 50, offset = 0, fields = listFields, nsfw = nsfw
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

    suspend fun getTopOneShots(
    ): NetworkResult<List<Work>> {
        return getRankingManga("oneshots")
    }

    suspend fun getTopPopularManga(): NetworkResult<List<Work>> {
        return getRankingManga("bypopularity")
    }

    suspend fun getMostFavoritedManga(): NetworkResult<List<Work>> {
        return getRankingManga("favorite")
    }

    suspend fun getTopDoujin(): NetworkResult<List<Work>> {
        return getRankingManga("doujin")
    }

    suspend fun getMangaDetails(
        id: Int,
    ): NetworkResult<Work> {
        val preferredTitleStyle = prefs.preferredTitleStyle.firstOrNull() ?: PreferredTitleStyle.PREFER_DEFAULT
        val result: NetworkResult<MangaForDetails> = handleApi {
            mangaService.getMangaDetails(
                id = id,
                fields = "id,title,main_picture,alternative_titles,start_date,end_date,created_at,updated_at,synopsis,mean,rank,popularity,authors,num_volumes,num_list_users,genres,media_type,status,my_list_status{updated_at,created_at,tags,comments,start_date,finish_date},num_chapters,pictures,related_anime,related_manga,recommendations,serialization"
            )
        }
        return when (result) {
            is NetworkResult.Success -> NetworkResult.Success(result.data.toDetailsWork(preferredTitleStyle))
            is NetworkResult.Error -> NetworkResult.Error(result.code, result.code.toErrorEnum())
            is NetworkResult.Exception -> NetworkResult.Exception(result.e)
        }
    }

}