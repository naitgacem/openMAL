package com.aitgacem.openmal.ui.fragments.home

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
class HomeViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val mangaRepository: MangaRepository,
) : ViewModel() {

    private var _topAiring = MutableLiveData<List<ItemForList>>()
    val topAiring: LiveData<List<ItemForList>> = _topAiring

    private var _topAnime = MutableLiveData<List<ItemForList>>()
    val topAnime: LiveData<List<ItemForList>> = _topAnime

    private var _topSpecial = MutableLiveData<List<ItemForList>>()
    val topSpecial: LiveData<List<ItemForList>> = _topSpecial


    private var _topManga = MutableLiveData<List<ItemForList>>()
    val topManga: LiveData<List<ItemForList>> = _topManga

    private var _topNovels = MutableLiveData<List<ItemForList>>()
    val topNovels: LiveData<List<ItemForList>> = _topNovels

    private var _topOneShots = MutableLiveData<List<ItemForList>>()
    val topOneShots: LiveData<List<ItemForList>> = _topOneShots



    init {
        viewModelScope.launch {
            _topAiring.postValue( animeRepository.getTopAiringAnime(limit = 50))
        }
        viewModelScope.launch {
            _topAnime.postValue(animeRepository.getTopTvAnime(limit = 50))
        }
        viewModelScope.launch {
            _topSpecial.postValue(animeRepository.getTopSpecialAnime(limit = 50))
        }
        viewModelScope.launch {
            _topManga.postValue( mangaRepository.getTopManga(limit = 50))
        }
        viewModelScope.launch {
            _topNovels.postValue(mangaRepository.getTopNovels(limit = 50))
        }
        viewModelScope.launch {
            _topOneShots.postValue(mangaRepository.getTopOneShots(limit = 50))
        }
    }
}

