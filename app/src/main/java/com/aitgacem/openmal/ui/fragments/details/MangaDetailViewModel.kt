package com.aitgacem.openmal.ui.fragments.details

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.aitgacem.openmalnet.data.MangaRepository
import com.aitgacem.openmalnet.models.MangaForDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MangaDetailViewModel @Inject constructor(
    val mangaRepository: MangaRepository,
    val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private var mangaDetail = MutableLiveData<MangaForDetails>()

    init {
        viewModelScope.launch {
            mangaDetail.postValue(
                mangaRepository.getMangaDetails(
                    savedStateHandle.get<Int>("id") ?: 0,
                    fields = "id, title, main_picture, alternative_titles, start_date, end_date, synopsis, mean, rank, popularity,genres,media_type,status,my_list_status,num_chapters,authors,pictures,related_anime,related_manga,recommendations,num_list_users"
                )
            )
        }
    }

    fun refresh(){
        val id = savedStateHandle.get<Int>("id")
        if(id != null){
            viewModelScope.launch {
                delay(2000)
                mangaDetail.postValue(
                    mangaRepository.getMangaDetails(
                        savedStateHandle.get<Int>("id") ?: 0,
                        fields = "id, title, main_picture, alternative_titles, start_date, end_date, synopsis, mean, rank, popularity,genres,media_type,status,my_list_status,num_chapters,authors,pictures,related_anime,related_manga,recommendations,num_list_users"
                    )
                )
            }
        }
    }

    fun getMangaUrl(): String{
        return "https://myanimelist.net/manga/${mangaDetail.value?.id}"
    }

    val publishingStatus = mangaDetail.map {
        formatPublishingStatus(it.status)
    }

    val displayTitle = mangaDetail.map {
        if(it.alternativeTitles?.en?.isNotEmpty() == true) it.alternativeTitles!!.en else it.title
    }

    val chapters = mangaDetail.map {
        if(it.numChapters > 0) "${it.numChapters} Chapters" else ""
    }
    val type = mangaDetail.map {
        it.mediaType
    }
    val ranking = mangaDetail.map { details ->
        details?.rank?.let {
            "Ranked #$it"
        }
    }
    val popularity = mangaDetail.map { details ->
        details?.popularity.let {
            "Popularity #$it"
        }
    }
    val members = mangaDetail.map { details ->
        details?.numListUsers?.let {
            "Members $it"
        }
    }

    val score = mangaDetail.map { details ->
        details?.mean?.let {
            "Score $it"
        }
    }
    val synopsys = mangaDetail.map {
        it.synopsis ?: ""
    }

    val genres = mangaDetail.map {
        it.genres
    }

    val publishing = mangaDetail.map {
        "${it.startDate} to ${it.endDate}"
    }

    val related = mangaDetail.map {
        it.relatedManga
    }
    val recommendations = mangaDetail.map {
        it.recommendations.map { it.node }
    }

    val readingStatus = mangaDetail.map {
        it.myListStatus?.status
    }
    val givenScore = mangaDetail.map {
        it.myListStatus?.score
    }
    val progress = mangaDetail.map {
        it.myListStatus?.numChaptersRead ?: 0
    }

    val numChapters = mangaDetail.map {
        it.numChapters
    }

    val readingStatusBundle = MediatorLiveData<Pair<Int, Int>>().apply {
        var progress = 0
        var max = 0

        addSource(this@MangaDetailViewModel.progress) { value ->
            progress = value
            val combinedValue = Pair(value, max)
            this.value = combinedValue

        }

        addSource(numChapters) { value ->
            max = value
            val combinedValue = Pair(progress, value)
            this.value = combinedValue
        }
    }

    private fun formatPublishingStatus(status: String): String {
        val words = status.split("_").map { it.capitalize() }
        return words.joinToString(" ")
    }

}

