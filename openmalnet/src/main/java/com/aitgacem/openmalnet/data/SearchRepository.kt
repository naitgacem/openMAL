package com.aitgacem.openmalnet.data

import com.aitgacem.openmalnet.api.mal.AnimeService
import com.aitgacem.openmalnet.api.mal.MangaService
import kotlinx.coroutines.flow.firstOrNull
import openmal.domain.NetworkResult
import openmal.domain.PreferredTitleStyle
import openmal.domain.Work
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val animeService: AnimeService,
    private val mangaService: MangaService,
    private val prefs: UserPreferencesRepository,
) {
    private val searchFields = "alternative_titles,mean,start_date,start_season"
    suspend fun searchAll(
        query: String,
        limit: Int? = null,
        offset: Int? = null,
    ): NetworkResult<List<Work>> {
        val preferredTitleStyle =
            prefs.preferredTitleStyle.firstOrNull() ?: PreferredTitleStyle.PREFER_DEFAULT
        val nsfw = prefs.isNsfwEnabledFlow.firstOrNull() ?: false
        val animeResults =
            handleApi { animeService.getAnimeList(query, limit, offset, searchFields, nsfw = nsfw) }
        val mangaResults =
            handleApi { mangaService.getMangaList(query, limit, offset, searchFields, nsfw = nsfw) }
        // When they both return an exception
        if (animeResults is NetworkResult.Exception && mangaResults is NetworkResult.Exception) {
            return NetworkResult.Exception(animeResults.e)
        }
        // When they both return an error
        if (animeResults is NetworkResult.Error && mangaResults is NetworkResult.Error) {
            return NetworkResult.Error(animeResults.code, animeResults.apiError)
        }

        // combine results
        val mangaList = if (mangaResults is NetworkResult.Success) {
            mangaResults.data.data.map { it.node }
                .map { it.toListWork(preferredTitleStyle) }
        } else {
            emptyList()
        }

        val animeList = if (animeResults is NetworkResult.Success) {
            animeResults.data.data.map { it.node }
                .map { it.toListWork(preferredTitleStyle) }
        } else {
            emptyList()
        }

        val combination = mutableListOf<Work>()
        val maxSize = maxOf(mangaList.size, animeList.size)
        for (i in 0 until maxSize) {
            if (i < mangaList.size) combination.add(mangaList[i])
            if (i < animeList.size) combination.add(animeList[i])
        }
        return NetworkResult.Success(combination)
    }

}