package com.aitgacem.openmal.ui.fragments.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.aitgacem.openmal.ui.components.WatchingStatus
import com.aitgacem.openmalnet.data.AnimeRepository
import com.aitgacem.openmalnet.data.UserRepository
import com.aitgacem.openmalnet.models.AnimeForDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditAnimeListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val animeRepository: AnimeRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    val id = savedStateHandle.get<Int>("id") ?: 0

    private val animeDetail = MutableLiveData<AnimeForDetails>()

    val listStatus = animeDetail.map {
        it.myListStatus
    }

    val maxEps = animeDetail.map {
        it?.numEpisodes
    }

    private var watchingStatus: String? = null
    private var progress: Int? = null
    private var givenScore: Int? = null
    private var startDate: String? = null
    private var finishDate: String? = null
    private var notes: String? = null

    init {
        viewModelScope.launch {
            animeDetail.postValue(
                animeRepository.getAnimeDetails(
                    id,
                    fields = "id,title,main_picture,alternative_titles,start_date,end_date,synopsis,mean,rank,popularity,num_list_users,genres,media_type,status,my_list_status{comments},num_episodes,start_season,broadcast,source,average_episode_duration,rating,studios,pictures,related_anime,related_manga,recommendations,statistics"
                )
            )
        }
    }

    fun updateWatchingStatus(status: WatchingStatus?) {
        watchingStatus = status?.name
    }

    fun updateProgress(str: String?) {
        str?.let {
            progress = try {
                Integer.parseInt(str)
            } catch (e: Exception) {
                null
            }

        }
    }

    fun updateGivenScore(score: Int) {
        givenScore = score
    }

    fun updateStartDate(dateStr: String?) {
        startDate = dateStr
    }

    fun updateFinishDate(dateStr: String?) {
        finishDate = dateStr
    }

    fun updateNotes(notes: String) {
        if (notes.isNotEmpty()) {
            this.notes = notes
        }
    }

    suspend fun save() {
        userRepository.updateAnimeListStatus(
            id = id,
            status = watchingStatus,
            numWatchedEps = progress,
            score = givenScore,
            startDate = startDate,
            finishDate = finishDate,
            comments = notes,
        )
    }

    suspend fun delete() {
        userRepository.deleteAnimeFromList(id)
    }
}