package com.aitgacem.openmal.ui.fragments.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitgacem.openmalnet.data.AnimeRepository
import com.aitgacem.openmalnet.models.AnimeForList
import com.aitgacem.openmalnet.models.ItemForList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    animeRepository: AnimeRepository,
) : ViewModel() {

    private var _searchTerm = MutableLiveData<String>()
    val searchTerm: LiveData<String> = _searchTerm


    private var _searchResults = MutableLiveData<List<ItemForList>>()
    val searchResults: LiveData<List<ItemForList>> = _searchResults

    init {
        _searchTerm.observeForever { searchTerm ->
            _searchResults.value = emptyList()
            if (searchTerm.length >= 3) {
                viewModelScope.launch {
                    val results = animeRepository.searchAnime(q = searchTerm, fields = "alternative_titles")
                    delay(100)
                    _searchResults.value = results
                }
            }
        }
    }
    fun updateSearchTerm(s: String?){
        _searchTerm.postValue(s ?: "")
    }
}