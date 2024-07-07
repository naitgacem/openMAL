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
import openmal.domain.Anime
import openmal.domain.Manga
import openmal.domain.NetworkResult
import openmal.domain.SortType
import openmal.domain.Work
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
) : ViewModel() {

    private val searchTerm = MutableSharedFlow<String>()
    private var cachedSearchResults: List<Work> = emptyList()
    private var transformedSearchResults: List<Work> = emptyList()
    private var _searchResults = MutableLiveData<NetworkResult<List<Work>>>()
    val searchResults: LiveData<NetworkResult<List<Work>>> = _searchResults
    private var _suggestions = MutableLiveData<List<Work>>(emptyList())
    val suggestions: LiveData<List<Work>> = _suggestions

    enum class SearchTypeFilter {
        ALL,
        ANIME,
        MANGA
    }

    private var searchType = SearchTypeFilter.ALL
    private var sortType = SortType.DEFAULT
    private var query = ""

    init {
        viewModelScope.launch {
            searchTerm
                .debounce(400)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.length >= 3) {
                        val results =
                            searchRepository.searchAll(query = query)
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
        this.query = query
        viewModelScope.launch {
            val results = searchRepository.searchAll(query = query)
            if (results is NetworkResult.Success) {
                cachedSearchResults = results.data
                applyTransformations()
            } else {
                _searchResults.postValue(results)
            }
        }
    }

    private fun applyTransformations() {
        // MUST maintain this order!!
        applySortOrder(cachedSearchResults)
        applyTypeFilter()
        _searchResults.postValue(NetworkResult.Success(transformedSearchResults))
    }

    private fun applySortOrder(currentResults: List<Work>) {
        val sortedResults = when (sortType) {
            SortType.SCORE -> currentResults.sortedByDescending { it.meanScore }
            SortType.START_DATE -> currentResults.sortedByDescending { it.startDate }
            else -> currentResults
        }
        transformedSearchResults = sortedResults
    }
    private fun applyTypeFilter() {
        val currentResults = transformedSearchResults
        val filteredResults = currentResults.filter {
            when (searchType) {
                SearchTypeFilter.ALL -> true
                SearchTypeFilter.ANIME -> it is Anime
                SearchTypeFilter.MANGA -> it is Manga
            }
        }
        transformedSearchResults = filteredResults
    }
    fun updateSortOrder(order: SortType){
        if(this.sortType != order){
            this.sortType = order
            applyTransformations()
        }
    }

    fun updateTypeFilter(searchTypeFilter: SearchTypeFilter){
        if(searchTypeFilter != this.searchType){
            this.searchType = searchTypeFilter
            applyTransformations()
        }
    }

    fun reset() {
        updateSearchTerm("")
        _searchResults.postValue(NetworkResult.Success(emptyList()))
        _suggestions.postValue(emptyList())
    }
}