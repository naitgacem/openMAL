package com.aitgacem.openmal.ui.fragments.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.aitgacem.openmal.ui.components.AnimeSortType
import com.aitgacem.openmal.ui.components.MangaSortType
import com.aitgacem.openmal.ui.components.ReadingStatus
import com.aitgacem.openmal.ui.components.WatchingStatus
import com.aitgacem.openmal.ui.generateHeader
import com.aitgacem.openmalnet.data.UserRepository
import com.aitgacem.openmalnet.models.UserAllOfAnimeStatistics
import com.aitgacem.openmalnet.models.UserAnimeListEdge
import com.aitgacem.openmalnet.models.UserMangaListEdge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    val animeList = MutableLiveData<List<UserAnimeListEdge>>()
    val mangaList = MutableLiveData<List<UserMangaListEdge>>()

    private val animeStats = MutableLiveData<UserAllOfAnimeStatistics>()

    init {
        loadUserAnimeList()
        loadUserMangaList()
        loadUserStats()
    }

    private fun loadUserStats() {
        viewModelScope.launch {
            val stats = userRepository.getMyUserInfo(fields = "anime_statistics")?.animeStatistics
            stats?.let { animeStats.postValue(it) }
        }
    }

    private fun loadUserAnimeList(status: String? = null, sortType: String? = null) {
        viewModelScope.launch {
            val userList = userRepository.getUserAnimeList(status = status, sort = sortType)
            animeList.postValue(userList?.data ?: emptyList())
        }
    }
    private fun loadUserMangaList(status: String? = null, sortType: String? = null) {
        viewModelScope.launch {
            val userList = userRepository.getUserMangaList(status = status, sort = sortType)
            mangaList.postValue(userList?.data ?: emptyList())
        }
    }

    private var animeFilter = MutableLiveData<WatchingStatus?>(null)
    private var mangaFilter = MutableLiveData<ReadingStatus?>(null)

    fun changeFilter(status: WatchingStatus?) {
        animeFilter.value = if (status == animeFilter.value) null else status
        loadUserAnimeList(animeFilter.value?.name, animeSort.value?.queryParam)
    }
    fun changeFilter(status: ReadingStatus?) {
        mangaFilter.value = if (status == mangaFilter.value) null else status
        loadUserMangaList(mangaFilter.value?.name, mangaSort.value?.queryParam)
    }

    private var animeSort = MutableLiveData<AnimeSortType?>(null)
    private var mangaSort = MutableLiveData<MangaSortType?>(null)
    fun changeSorting(type: AnimeSortType?) {
        animeSort.value = if (type == animeSort.value) null else type
        loadUserAnimeList(animeFilter.value?.name, type?.queryParam)
    }
    fun changeSorting(type: MangaSortType?) {
        mangaSort.value = if (type == mangaSort.value) null else type
        loadUserMangaList(mangaFilter.value?.name, type?.queryParam)
    }

    private var _animeHeader = MediatorLiveData<String>().apply {
        addSource(animeFilter){
            value = generateHeader(it, animeStats.value)
        }
        addSource(animeStats){
            value = generateHeader(animeFilter.value, it)
        }
    }
    val animeHeader: LiveData<String> = _animeHeader

    private var _mangaHeader = mangaFilter.map {
        it?.displayName ?: "All Manga"
    }
    val mangaHeader: LiveData<String> = _mangaHeader

    fun refresh() {
        viewModelScope.launch {
            delay(2000)
            loadUserAnimeList()
            loadUserMangaList()
        }
        viewModelScope.launch {
            delay(2000)
            loadUserStats()
            loadUserMangaList()
        }
    }

}