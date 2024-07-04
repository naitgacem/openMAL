package com.aitgacem.openmal.ui.fragments.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitgacem.openmalnet.data.AnimeRepository
import com.aitgacem.openmalnet.data.MangaRepository
import com.aitgacem.openmalnet.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import openmal.domain.ListStatus
import openmal.domain.MediaType
import openmal.domain.NetworkResult
import openmal.domain.UserListStatus
import openmal.domain.Work
import javax.inject.Inject


@HiltViewModel
class EditListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    animeRepository: AnimeRepository,
    mangaRepository: MangaRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    val id: Int = savedStateHandle["id"] ?: 1
    private val type: MediaType = savedStateHandle["type"] ?: MediaType.ANIME
    private var status = ListStatus.NON_EXISTENT
    private var progressCount: Int? = null
    private var score: Int? = null
    private var startDate: String? = null
    private var finishDate: String? = null
    private var comments: String? = null

    private var _work = MutableLiveData<NetworkResult<Work>>()
    val work: LiveData<NetworkResult<Work>> = _work

    private var _listStatus = MutableLiveData<UserListStatus?>()
    val listStatus: LiveData<UserListStatus?> = _listStatus

    init {
        viewModelScope.launch {
            val result = when (type) {
                MediaType.ANIME -> animeRepository.getAnimeDetails(id)
                MediaType.MANGA -> mangaRepository.getMangaDetails(id)
            }
            _work.postValue(result)
            if (result is NetworkResult.Success) {
                _listStatus.postValue(result.data.listStatus)
            }
        }
    }

    suspend fun save(): NetworkResult<*> {
        return when (type) {
            MediaType.ANIME -> {
                userRepository.updateAnimeListStatus(
                    id = id,
                    status = status,
                    numWatchedEps = progressCount,
                    score = score,
                    startDate = startDate,
                    finishDate = finishDate,
                    comments = comments,
                )
            }

            MediaType.MANGA -> {
                userRepository.updateMangaListStatus(
                    id = id,
                    status = status,
                    numReadChaps = progressCount,
                    score = score,
                    startDate = startDate,
                    finishDate = finishDate,
                    comments = comments,
                )
            }
        }

    }

    suspend fun delete(): NetworkResult<Unit> {
        return when (type) {
            MediaType.ANIME ->
                userRepository.deleteAnimeFromList(id)

            MediaType.MANGA ->
                userRepository.deleteMangaFromList(id)
        }
    }

    fun updateProgress(str: String?) {
        str?.let {
            progressCount = try {
                Integer.parseInt(str)
            } catch (e: Exception) {
                null
            }
        }
    }

    fun markProgressFinished() {
        val total = (_work.value as? NetworkResult.Success<Work>)?.data?.numReleases
        if (total != null && total > 0) {
            progressCount = total
        }
    }

    fun updateGivenScore(score: Int?) {
        this.score = score
    }

    fun updateStartDate(dateStr: String?) {
        startDate = dateStr
    }

    fun updateFinishDate(dateStr: String?) {
        finishDate = dateStr
    }

    fun updateNotes(notes: String) {
        if (notes.isNotBlank()) {
            this.comments = notes
        }
    }

    fun updateStatus(status: ListStatus) {
        this.status = status
    }
}