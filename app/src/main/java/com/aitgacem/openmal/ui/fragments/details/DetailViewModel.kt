package com.aitgacem.openmal.ui.fragments.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.aitgacem.openmalnet.data.AnimeRepository
import com.aitgacem.openmalnet.data.MangaRepository
import com.aitgacem.openmalnet.data.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import openmal.domain.MediaType
import openmal.domain.NetworkResult
import openmal.domain.Work
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animeRepository: AnimeRepository,
    private val mangaRepository: MangaRepository,
    prefs: UserPreferencesRepository,
) : ViewModel() {

    val id = savedStateHandle.get<Int>("id")!!
    private val workType = savedStateHandle.get<MediaType>("type")
    val isLogged = liveData {
        val value = prefs.isLoggedInFlow.firstOrNull()
        emit(value)
    }
    private var _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    val state = MutableLiveData<NetworkResult<Work>>()
    val work = MutableLiveData<Work>()

    init {
        refresh()
    }

    fun refresh() {
        _isRefreshing.postValue(true)
        viewModelScope.launch {
            val result = if (workType == MediaType.ANIME) {
                animeRepository.getAnimeDetails(id = id)
            } else {
                mangaRepository.getMangaDetails(id = id)
            }
            _isRefreshing.postValue(false)
            state.postValue(result)
            if(result is NetworkResult.Success){
                work.postValue(result.data)
            }
        }
    }

}