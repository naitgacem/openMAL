package com.aitgacem.openmal.ui.fragments.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitgacem.openmalnet.data.AnimeRepository
import com.aitgacem.openmalnet.data.MangaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import openmal.domain.NetworkResult
import openmal.domain.Work
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val mangaRepository: MangaRepository,
) : ViewModel() {

    private var _suggestedAnime = MutableLiveData<NetworkResult<List<Work>>>()
    val suggestedAnime: LiveData<NetworkResult<List<Work>>> = _suggestedAnime

    private var _topPopularAnime = MutableLiveData<NetworkResult<List<Work>>>()
    val topPopularAnime: LiveData<NetworkResult<List<Work>>> = _topPopularAnime

    private var _upcomingAnime = MutableLiveData<NetworkResult<List<Work>>>()
    val upcomingAnime: LiveData<NetworkResult<List<Work>>> = _upcomingAnime


    private var _topPopularManga = MutableLiveData<NetworkResult<List<Work>>>()
    val topPopularManga: LiveData<NetworkResult<List<Work>>> = _topPopularManga

    private var _mostFavouritedManga = MutableLiveData<NetworkResult<List<Work>>>()
    val mostFavouritedManga: LiveData<NetworkResult<List<Work>>> = _mostFavouritedManga

    private var _topDoujin = MutableLiveData<NetworkResult<List<Work>>>()
    val topDoujin: LiveData<NetworkResult<List<Work>>> = _topDoujin



    init {
        viewModelScope.launch {
            _suggestedAnime.postValue( animeRepository.getTopSuggestedAnime())
        }
        viewModelScope.launch {
            _topPopularAnime.postValue(animeRepository.getTopPopularAnime())
        }
        viewModelScope.launch {
            _upcomingAnime.postValue(animeRepository.getTopUpcomingAnime())
        }

        viewModelScope.launch {
            _topPopularManga.postValue( mangaRepository.getTopPopularManga())
        }
        viewModelScope.launch {
            _mostFavouritedManga.postValue(mangaRepository.getMostFavoritedManga())
        }
        viewModelScope.launch {
            _topDoujin.postValue(mangaRepository.getTopDoujin())
        }
    }

}

