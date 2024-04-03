package com.aitgacem.openmalnet.data

import com.aitgacem.openmalnet.api.mal.AnimeService
import com.aitgacem.openmalnet.models.AnimeForDetails
import com.aitgacem.openmalnet.models.AnimeList
import com.aitgacem.openmalnet.models.ItemForList
import javax.inject.Inject

class AnimeRepository @Inject constructor(
    private val animeService: AnimeService
) {

    suspend fun getTopAiringAnime(
        limit: Int? = null,
        offset: Int? = null,
        fields: String? = null,
    ): List<ItemForList>? {
        return getTopAnime(AnimeRanking.AIRING, limit, offset, fields)
    }

    suspend fun getTopTvAnime(
        limit: Int? = null,
        offset: Int? = null,
        fields: String? = null,
    ): List<ItemForList>? {
        return getTopAnime(AnimeRanking.TV, limit, offset, fields)
    }

    suspend fun getTopSpecialAnime(
        limit: Int? = null,
        offset: Int? = null,
        fields: String? = null,
    ): List<ItemForList>? {
        return getTopAnime(AnimeRanking.SPECIAL, limit, offset, fields)
    }

    suspend fun getTopSuggestedAnime(
        limit: Int? = null,
        offset: Int? = null,
        fields: String? = null,
    ): List<ItemForList>? {
        return getSuggestedAnime()
    }

    suspend fun getTopPopularAnime(
        limit: Int? = null,
        offset: Int? = null,
        fields: String? = null,
    ): List<ItemForList>? {
        return getTopAnime(AnimeRanking.BY_POPULARITY, limit, offset, fields)
    }

    suspend fun getTopUpcomingAnime(
        limit: Int? = null,
        offset: Int? = null,
        fields: String? = null,
    ): List<ItemForList>? {
        return getTopAnime(AnimeRanking.UPCOMING, limit, offset, fields)
    }

    private suspend fun getTopAnime(
        rankingType: AnimeRanking, limit: Int? = null,
        offset: Int? = null,
        fields: String? = null,
    ): List<ItemForList>? {
        return try {
            animeService.getAnimeRanking(
                rankingType.queryParam, limit, offset, fields
            ).data?.map { it.node as ItemForList }
        } catch (e: Exception) {
            e.printStackTrace()
            print(e.message)
            null
        }
    }

    private suspend fun getSuggestedAnime(
        limit: Int? = null,
        offset: Int? = null,
        fields: String? = null,
    ): List<ItemForList>? {
        return try {
            animeService.getAnimeSuggestions(
                limit, offset, fields
            ).data.map { it.node as ItemForList }
        } catch (e: Exception) {
            e.printStackTrace()
            print(e.message)
            null
        }
    }

    suspend fun getSeasonAnime(
        year: Int,
        season: String,
        sort: String? = null,
        limit: Int? = null,
        offset: Int? = null,
        fields: String? = null,
    ): List<ItemForList>? {
        return try {
            animeService.getAnimeBySeason(
                year, season, sort,
                limit, offset, fields
            ).data.map { it.node as ItemForList }
        } catch (e: Exception) {
            e.printStackTrace()
            print(e.message)
            null
        }
    }

    suspend fun getAnimeDetails(
        id: Int,
        fields: String? = null,
    ): AnimeForDetails? {
        return try {
            animeService.getAnimeDetails(id, fields)
        } catch (e: Exception) {
            e.printStackTrace()
            print(e.message)
            null
        }
    }

    suspend fun searchAnime(
        q: String = "",
        limit: Int? = null,
        offset: Int? = null,
        fields: String? = null,
    ): List<ItemForList> {
        return try {
            animeService.getAnimeList(q, limit, offset, fields).data.map { it.node as ItemForList }
        } catch (e: Exception) {
            e.printStackTrace()
            print(e.message)
            emptyList()
        }
    }
}