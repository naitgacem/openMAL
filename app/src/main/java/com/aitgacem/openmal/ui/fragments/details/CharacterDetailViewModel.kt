package com.aitgacem.openmal.ui.fragments.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitgacem.openmalnet.data.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import openmal.domain.Character
import openmal.domain.NetworkResult
import javax.inject.Inject

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val id: Int = savedStateHandle["id"] ?: throw IllegalStateException("ID missing")
    private var _character = MutableLiveData<NetworkResult<Character>>()
    val character: LiveData<NetworkResult<Character>> = _character

    init {
        viewModelScope.launch {
            val result = animeRepository.getAnimeCharacterDetails(id)
            _character.postValue(result)
        }
    }
}