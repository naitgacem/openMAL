package com.aitgacem.openmal.ui.fragments.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitgacem.openmalnet.data.UserPreferencesRepository
import com.aitgacem.openmalnet.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
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
    private val prefs: UserPreferencesRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val mediaType =
        savedStateHandle.get<MediaType>("type") ?: throw IllegalStateException("Type not found")

    private var _workList = MutableLiveData<NetworkResult<List<Work>>>()
    val workList: LiveData<NetworkResult<List<Work>>> = _workList

    private var _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private var _animeStats = MutableLiveData<Map<ListStatus, Int>>()
    val animeStats: LiveData<Map<ListStatus, Int>> = _animeStats


    private var _numEpisodesTotal = MutableLiveData<Int>()
    val numEpisodesTotal: LiveData<Int> = _numEpisodesTotal


    val filterChipIds = mutableMapOf<Int, ListStatus>()
    private var _filter = MutableLiveData(ListStatus.NON_EXISTENT)
    val filter = _filter
    private var sort = MutableLiveData(SortType.DEFAULT)

    private val isNsfwEnabled = prefs.isNsfwEnabledFlow

    init {
        refresh()
        refreshIfNeeded()
    }


    private fun loadUserWorkList() {
        viewModelScope.launch {
            val nsfw = isNsfwEnabled.firstOrNull() ?: false
            val userList = when (mediaType) {
                MediaType.ANIME -> {
                    userRepository.getUserAnimeList(
                        status = _filter.value!!, sort = sort.value!!, nsfw = nsfw
                    )
                }

                MediaType.MANGA -> {
                    userRepository.getUserMangaList(
                        status = _filter.value!!, sort = sort.value!!, nsfw = nsfw,
                    )
                }
            }
            _workList.postValue(userList)
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
        _filter.value = status
        loadUserWorkList()
    }


    fun changeSorting(type: SortType) {
        sort.value = type
        loadUserWorkList()
    }

    fun refresh() {
        loadUserWorkList()
        loadUserStats()
    }

    private fun refreshIfNeeded() {
        viewModelScope.launch {
            isNsfwEnabled.distinctUntilChanged().collect { _ ->
                refresh()
            }
        }
    }

    fun setLoading(state: Boolean) {
        _isLoading.postValue(state)
    }
}