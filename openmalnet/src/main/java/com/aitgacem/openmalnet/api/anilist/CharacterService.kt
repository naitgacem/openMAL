package com.aitgacem.openmalnet.api.anilist

import com.aitgacem.openmalnet.models.AnimeCharactersQuery
import com.aitgacem.openmalnet.models.CharacterDetailsQuery
import com.aitgacem.openmalnet.models.MangaCharactersQuery
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Optional

class CharacterService(
    private val apolloClient: ApolloClient
){
    suspend fun getAnimeCharacters(malId: Int): ApolloResponse<AnimeCharactersQuery.Data> {
        return apolloClient.query(AnimeCharactersQuery(Optional.present(malId))).execute()
    }
    suspend fun getMangaCharacters(malId: Int): ApolloResponse<MangaCharactersQuery.Data> {
        return apolloClient.query(MangaCharactersQuery(Optional.present(malId))).execute()
    }
    suspend fun getCharacterDetails(id: Int): ApolloResponse<CharacterDetailsQuery.Data> {
        return apolloClient.query(CharacterDetailsQuery(Optional.present(id))).execute()
    }
}