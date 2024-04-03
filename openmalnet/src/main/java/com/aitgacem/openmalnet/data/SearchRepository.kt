package com.aitgacem.openmalnet.data

import com.aitgacem.openmalnet.api.mal.AnimeService
import com.aitgacem.openmalnet.api.mal.MangaService
import com.aitgacem.openmalnet.models.AnimeList
import com.aitgacem.openmalnet.models.MangaList
import openmal.domain.NetworkResult
import openmal.domain.Work
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val animeService: AnimeService,
    private val mangaService: MangaService,
) {
    private val searchFields = "alternative_titles,mean"
    suspend fun search(
        query: String,
        limit: Int? = null,
        offset: Int? = null,
    ): NetworkResult<List<Work>> {
        val animeResults: NetworkResult<AnimeList> =
            handleApi { animeService.getAnimeList(query, limit, offset, searchFields) }
        val mangaResults: NetworkResult<MangaList> =
            handleApi { mangaService.getMangaList(query, limit, offset, searchFields) }
        val result = mutableListOf<Work>()
        // When they both return an exception
        if (animeResults is NetworkResult.Exception && mangaResults is NetworkResult.Exception) {
            return NetworkResult.Exception(animeResults.e)
        }
        // When they both return an error
        if (animeResults is NetworkResult.Error && mangaResults is NetworkResult.Error) {
            return NetworkResult.Error(animeResults.code, animeResults.apiError)
        }

        // combine results
        if (mangaResults is NetworkResult.Success) {
            result.addAll(mangaResults.data.data.map { it.node }.map { it.toListWork() })
        }
        if (animeResults is NetworkResult.Success) {
            result.addAll(animeResults.data.data.map { it.node }.map { it.toListWork() })
        }
        return NetworkResult.Success(result.sortedByDescending { it.meanScore })
    }
}