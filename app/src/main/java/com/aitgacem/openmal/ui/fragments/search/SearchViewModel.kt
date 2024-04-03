package com.aitgacem.openmal.ui.fragments.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitgacem.openmalnet.data.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import openmal.domain.NetworkResult
import openmal.domain.Work
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
) : ViewModel() {

    private val searchTerm = MutableSharedFlow<String>()
    private var _searchResults = MutableLiveData<NetworkResult<List<Work>>>()
    val searchResults: LiveData<NetworkResult<List<Work>>> = _searchResults
    private var _suggestions = MutableLiveData<List<Work>>(emptyList())
    val suggestions: LiveData<List<Work>> = _suggestions

    init {
        viewModelScope.launch {
            searchTerm
                .debounce(400)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.length >= 3) {
                        val results =
                            searchRepository.search(query = query)
                        if (results is NetworkResult.Success) {
                            _suggestions.postValue(
                                results.data
                            )
                        }
                    }
                }
        }
    }

    fun updateSearchTerm(s: String) {
        viewModelScope.launch {
            searchTerm.emit(s)
        }
    }

    fun doSearch(query: String) {
        viewModelScope.launch {
            val results = searchRepository.search(query = query)
            _searchResults.value = results
        }
    }

    fun reset() {
        updateSearchTerm("")
        _searchResults.postValue(NetworkResult.Success(emptyList()))
        _suggestions.postValue(emptyList())
    }
}