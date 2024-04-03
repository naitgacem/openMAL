package com.aitgacem.openmal.ui.fragments.seasonal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitgacem.openmalnet.data.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import openmal.domain.NetworkResult
import openmal.domain.Season
import openmal.domain.Work
import javax.inject.Inject

@HiltViewModel
class SeasonViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    private val animeRepository: AnimeRepository,
): ViewModel() {

    private val position: Pair<Season, Int>? = savedStateHandle["position"]

    private var _list = MutableLiveData<NetworkResult<List<Work>>>()
    val list: LiveData<NetworkResult<List<Work>>> = _list

    init {
       refresh()
    }
    fun refresh(){
        viewModelScope.launch {
            position?.let {
                val result = animeRepository.getSeasonAnime(position.second, position.first.name.lowercase())
                _list.postValue(result)
            }
        }
    }
}