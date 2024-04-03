package com.aitgacem.openmal.ui.fragments.home

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
class HomeViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val mangaRepository: MangaRepository,
) : ViewModel() {

    private var _topAiringAnime = MutableLiveData<NetworkResult<List<Work>>>()
    val topAiringAnime: LiveData<NetworkResult<List<Work>>> = _topAiringAnime

    private var _topAnime = MutableLiveData<NetworkResult<List<Work>>>()
    val topAnime: MutableLiveData<NetworkResult<List<Work>>> = _topAnime

    private var _topSpecial = MutableLiveData<NetworkResult<List<Work>>>()
    val topSpecial: MutableLiveData<NetworkResult<List<Work>>> = _topSpecial


    private var _topManga = MutableLiveData<NetworkResult<List<Work>>>()
    val topManga: MutableLiveData<NetworkResult<List<Work>>> = _topManga

    private var _topNovels = MutableLiveData<NetworkResult<List<Work>>>()
    val topNovels: MutableLiveData<NetworkResult<List<Work>>> = _topNovels

    private var _topOneShots = MutableLiveData<NetworkResult<List<Work>>>()
    val topOneShots: MutableLiveData<NetworkResult<List<Work>>> = _topOneShots


    init {
        viewModelScope.launch {
            _topAiringAnime.postValue(
                animeRepository.getTopAiringAnime()
            )
        }
        viewModelScope.launch {
            _topAnime.postValue(animeRepository.getTopTvAnime())
        }
        viewModelScope.launch {
            _topSpecial.postValue(animeRepository.getTopSpecialAnime())
        }
        viewModelScope.launch {
            _topManga.postValue(mangaRepository.getTopManga())
        }
        viewModelScope.launch {
            _topNovels.postValue(mangaRepository.getTopNovels())
        }
        viewModelScope.launch {
            _topOneShots.postValue(mangaRepository.getTopOneShots())
        }
    }
}

