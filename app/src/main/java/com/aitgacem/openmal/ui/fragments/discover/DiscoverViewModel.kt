package com.aitgacem.openmal.ui.fragments.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitgacem.openmalnet.data.AnimeRepository
import com.aitgacem.openmalnet.data.MangaRepository
import com.aitgacem.openmalnet.models.ItemForList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val mangaRepository: MangaRepository,
) : ViewModel() {

    private var _suggestedAnime = MutableLiveData<List<ItemForList>>()
    val suggestedAnime: LiveData<List<ItemForList>> = _suggestedAnime

    private var _topPopularAnime = MutableLiveData<List<ItemForList>>()
    val topPopularAnime: LiveData<List<ItemForList>> = _topPopularAnime

    private var _upcomingAnime = MutableLiveData<List<ItemForList>>()
    val upcomingAnime: LiveData<List<ItemForList>> = _upcomingAnime


    private var _topPopularManga = MutableLiveData<List<ItemForList>>()
    val topPopularManga: LiveData<List<ItemForList>> = _topPopularManga

    private var _mostFavouritedManga = MutableLiveData<List<ItemForList>>()
    val mostFavouritedManga: LiveData<List<ItemForList>> = _mostFavouritedManga

    private var _topDoujin = MutableLiveData<List<ItemForList>>()
    val topDoujin: LiveData<List<ItemForList>> = _topDoujin



    init {
        viewModelScope.launch {
            _suggestedAnime.postValue( animeRepository.getTopSuggestedAnime(limit = 50))
        }
        viewModelScope.launch {
            _topPopularAnime.postValue(animeRepository.getTopPopularAnime(limit = 50))
        }
        viewModelScope.launch {
            _upcomingAnime.postValue(animeRepository.getTopUpcomingAnime(limit = 50))
        }

        viewModelScope.launch {
            _topPopularManga.postValue( mangaRepository.getTopPopularManga(limit = 50))
        }
        viewModelScope.launch {
            _mostFavouritedManga.postValue(mangaRepository.getMostFavoritedManga(limit = 50))
        }
        viewModelScope.launch {
            _topDoujin.postValue(mangaRepository.getTopDoujin(limit = 50))
        }
    }

}

