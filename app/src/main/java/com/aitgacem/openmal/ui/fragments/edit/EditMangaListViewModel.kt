package com.aitgacem.openmal.ui.fragments.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.aitgacem.openmal.ui.components.ReadingStatus
import com.aitgacem.openmalnet.data.MangaRepository
import com.aitgacem.openmalnet.data.UserRepository
import com.aitgacem.openmalnet.models.MangaForDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EditMangaListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val mangaRepository: MangaRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    val id = savedStateHandle.get<Int>("id") ?: 0

    private val mangaDetail = MutableLiveData<MangaForDetails>()

    val listStatus = mangaDetail.map {
        it.myListStatus
    }

    val maxChps = mangaDetail.map {
        it?.numChapters
    }

    private var readingStatus: String? = null
    private var progress: Int? = null
    private var givenScore: Int? = null
    private var startDate: String? = null
    private var finishDate: String? = null
    private var notes: String? = null

    init {
        viewModelScope.launch {
            mangaDetail.postValue(
                mangaRepository.getMangaDetails(
                    id,
                    fields = "id, title, main_picture, alternative_titles, start_date, end_date, synopsis, mean, rank, popularity,genres,media_type,status,my_list_status{comments},num_chapters,authors,pictures,related_anime,related_manga,recommendations,num_list_users"
                )
            )
        }
    }

    fun updateWatchingStatus(status: ReadingStatus?) {
        readingStatus = status?.name
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
        userRepository.updateMangaListStatus(
            id = id,
            status = readingStatus,
            numReadChaps = progress,
            score = givenScore,
            startDate = startDate,
            finishDate = finishDate,
            comments = notes,
        )
    }

    suspend fun delete() {
        userRepository.deleteMangaFromList(id)
    }
}