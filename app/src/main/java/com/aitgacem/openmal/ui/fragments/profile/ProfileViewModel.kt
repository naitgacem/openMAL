package com.aitgacem.openmal.ui.fragments.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitgacem.openmalnet.data.UserPreferencesRepository
import com.aitgacem.openmalnet.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import openmal.domain.ListStatus
import openmal.domain.MediaType
import openmal.domain.NetworkResult
import openmal.domain.SortType
import openmal.domain.User
import openmal.domain.Work
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    prefs: UserPreferencesRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val mediaType =
        savedStateHandle.get<MediaType>("type") ?: throw IllegalStateException("Type not found")

    private var _workList = MutableLiveData<NetworkResult<List<Work>>>()
    val workList: LiveData<NetworkResult<List<Work>>> = _workList

    private var _animeStats = MutableLiveData<Map<ListStatus, Int>>()
    val animeStats: LiveData<Map<ListStatus, Int>> = _animeStats

    private var _numEpisodesTotal = MutableLiveData<Int>()
    val numEpisodesTotal: LiveData<Int> = _numEpisodesTotal

    private var _filter = MutableLiveData(ListStatus.NON_EXISTENT)
    val filter = _filter
    private var sort = MutableLiveData(SortType.DEFAULT)

    private val isNsfwEnabled = prefs.isNsfwEnabledFlow
    private val preferredTitleStyle = prefs.preferredTitleStyle

    private var _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    init {
        refresh()
        refreshIfNeeded()
    }


    private fun loadUserWorkList() {
        _isRefreshing.value = true
        viewModelScope.launch {
            val result = when (mediaType) {
                MediaType.ANIME -> {
                    userRepository.getUserAnimeList(
                        status = _filter.value!!, sort = sort.value!!
                    )
                }

                MediaType.MANGA -> {
                    userRepository.getUserMangaList(
                        status = _filter.value!!, sort = sort.value!!
                    )
                }
            }
            _workList.postValue(result)
            _isRefreshing.value = false
        }
    }

    private fun loadUserStats() {
        viewModelScope.launch {
            when (val user: NetworkResult<User> = userRepository.getMyUserInfo()) {
                is NetworkResult.Success -> {
                    val stats = user.data.animeStats
                    _numEpisodesTotal.postValue(user.data.numEpisodes)
                    _animeStats.postValue(stats)
                }

                else -> {}
            }
        }
    }

    fun changeFilter(status: ListStatus) {
        if (_filter.value != status) {
            _filter.value = status
            loadUserWorkList()
        }
    }


    fun changeSorting(type: SortType) {
        if (sort.value != type) {
            sort.value = type
            loadUserWorkList()
        }
    }

    fun refresh() {
        loadUserWorkList()
        loadUserStats()
    }

    private fun refreshIfNeeded() {
        val combination = combine(isNsfwEnabled, preferredTitleStyle) { _, _ ->
            //nothing
        }
        viewModelScope.launch {
            combination.collect {
                refresh()
            }
        }
    }

}