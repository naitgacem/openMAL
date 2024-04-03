package com.aitgacem.openmalnet.data


import com.aitgacem.openmalnet.api.mal.MangaService
import com.aitgacem.openmalnet.models.ItemForList
import com.aitgacem.openmalnet.models.MangaForDetails
import javax.inject.Inject

class MangaRepository @Inject constructor(
    private val mangaService: MangaService
) {

    suspend fun getTopManga(
        limit: Int? = null,
        offset: Int? = null,
        fields: String? = null,
    ): List<ItemForList>? {
        return getTopManga(MangaRanking.MANGA, limit, offset, fields)
    }

    suspend fun getTopNovels(
        limit: Int? = null,
        offset: Int? = null,
        fields: String? = null,
    ): List<ItemForList>? {
        return getTopManga(MangaRanking.NOVELS, limit, offset, fields)
    }

    suspend fun getTopOneShots(
        limit: Int? = null,
        offset: Int? = null,
        fields: String? = null,
    ): List<ItemForList>? {
        return getTopManga(MangaRanking.ONESHOTS, limit, offset, fields)
    }
    suspend fun getTopPopularManga(
        limit: Int? = null,
        offset: Int? = null,
        fields: String? = null,
    ): List<ItemForList>? {
        return getTopManga(MangaRanking.BY_POPULARITY, limit, offset, fields)
    }
    suspend fun getMostFavoritedManga(
        limit: Int? = null,
        offset: Int? = null,
        fields: String? = null,
    ): List<ItemForList>? {
        return getTopManga(MangaRanking.FAVORITE, limit, offset, fields)
    }
    suspend fun getTopDoujin(
        limit: Int? = null,
        offset: Int? = null,
        fields: String? = null,
    ): List<ItemForList>? {
        return getTopManga(MangaRanking.DOUJIN, limit, offset, fields)
    }

    private suspend fun getTopManga(
        rankingType: MangaRanking, limit: Int? = null,
        offset: Int? = null,
        fields: String? = null,
    ): List<ItemForList>? {
        return try {
            mangaService.getMangaRanking(
                rankingType.queryParam, limit, offset, fields
            ).data.map { it.node as ItemForList }
        } catch (e: Exception){
            e.printStackTrace()
            print(e.message)
            null
        }
    }

    suspend fun getMangaDetails(
        id: Int,
        fields: String? = null,
    ): MangaForDetails? {
        return try {
            mangaService.getMangaDetails(id, fields)
        } catch (e: Exception) {
            e.printStackTrace()
            print(e.message)
            null
        }
    }
}