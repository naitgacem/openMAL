package com.aitgacem.openmal.ui.fragments.details

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.aitgacem.openmalnet.data.AnimeRepository
import com.aitgacem.openmalnet.models.AnimeForDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimeDetailViewModel @Inject constructor(
    val animeRepository: AnimeRepository,
    val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var animeDetail = MutableLiveData<AnimeForDetails>()

    init {
        viewModelScope.launch {
            animeDetail.postValue(
                animeRepository.getAnimeDetails(
                    savedStateHandle.get<Int>("id") ?: 0,
                    fields = "id, title, main_picture, alternative_titles,start_date, end_date, synopsis, mean, rank, popularity,num_list_users,genres,media_type,status,my_list_status,num_episodes,start_season,broadcast,source,average_episode_duration,rating,studios,pictures,related_anime,related_manga,recommendations,statistics"
                )
            )
        }
    }

    fun getAnimeUrl(): String{
        return "https://myanimelist.net/anime/${animeDetail.value?.id}"
    }

    fun refresh(){
        val id = savedStateHandle.get<Int>("id")
        if(id != null){
            viewModelScope.launch {
                delay(2000)
                animeDetail.postValue(
                    animeRepository.getAnimeDetails(
                        savedStateHandle.get<Int>("id") ?: 0,
                        fields = "id, title, main_picture, alternative_titles,start_date, end_date, synopsis, mean, rank, popularity,num_list_users,genres,media_type,status,my_list_status,num_episodes,start_season,broadcast,source,average_episode_duration,rating,studios,pictures,related_anime,related_manga,recommendations,statistics"
                    )
                )
            }
        }
    }

    val displayTitle = animeDetail.map {
        if(it?.alternativeTitles?.en?.isNotEmpty() == true) it.alternativeTitles!!.en else it?.title
    }
    val airingStatus = animeDetail.map {
        formatAiringStatus(it?.status ?: "")
    }
    val season = animeDetail.map { season ->
        season?.startSeason?.let {
            "${it.season.name}, ${it.year}"
        }
    }
    val episodes = animeDetail.map {
        val sb = StringBuilder()
        if ((it?.numEpisodes ?: 0) > 0) sb.append(it?.numEpisodes).append(" ep ") else sb.append("? ep")
        val seconds = it?.averageEpisodeDuration
        if (seconds != null && seconds > 0) {
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            sb.append(
                when {
                    hours > 0 -> String.format(", %dh, %d min", hours, minutes)
                    else -> String.format(", %d min", minutes)
                }
            )
        }
        sb.toString()
    }
    val ratingType = animeDetail.map {
        "${it?.mediaType?.uppercase()}, ${it?.rating?.uppercase()}"
    }
    val raking = animeDetail.map { details ->
        details?.rank?.let {
            "Ranked #$it"
        }
    }
    val popularity = animeDetail.map { details ->
        details?.popularity.let {
            "Popularity #$it"
        }
    }
    val members = animeDetail.map { details ->
        details?.numListUsers?.let {
            "Members $it"
        }
    }

    val score = animeDetail.map { details ->
        details?.mean?.let {
            "Score $it"
        }
    }
    val synopsys = animeDetail.map {
        it?.synopsis ?: ""
    }

    val genres = animeDetail.map {
        it?.genres ?: emptyList()
    }

    val airDate = animeDetail.map {
        "${it?.startDate} to ${it?.endDate}"
    }

    val broadcast = animeDetail.map { details ->
        details?.broadcast?.let {
            "${it.dayOfTheWeek}s, at ${it.startTime}"
        }
    }
    val source = animeDetail.map {
        it?.source ?: "Unknown"
    }
    val studios = animeDetail.map { details ->
        details?.studios?.joinToString {
            it.name
        }
    }
    val related = animeDetail.map {
        it?.relatedAnime ?: emptyList()
    }
    val recommendations = animeDetail.map {
        it?.recommendations?.map { it.node }
    }

    val watchingStatus = animeDetail.map {
        it?.myListStatus?.status
    }
    val givenScore = animeDetail.map {
        it?.myListStatus?.score
    }
    val progress = animeDetail.map {
        it?.myListStatus?.numEpisodesWatched ?: 0
    }

    val numEpisodes = animeDetail.map {
        it?.numEpisodes
    }

    val watchingStatusBundle = MediatorLiveData<Pair<Int, Int>>().apply {
        var progress: Int = 0
        var max: Int = 0

        addSource(this@AnimeDetailViewModel.progress) { value ->
            progress = value
            val combinedValue = Pair(value, max)
            this.value = combinedValue

        }

        addSource(numEpisodes) { value ->
            max = value ?: 0
            val combinedValue = Pair(progress, max)
            this.value = combinedValue
        }
    }

    private fun formatAiringStatus(status: String): String {
        val words = status.split("_").map { it.capitalize() }
        return words.joinToString(" ")
    }

}

