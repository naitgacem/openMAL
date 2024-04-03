package com.aitgacem.openmal.ui.fragments.seasonal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitgacem.openmal.data.model.Season
import com.aitgacem.openmal.data.model.SeasonYear
import com.aitgacem.openmalnet.data.AnimeRepository
import com.aitgacem.openmalnet.data.MangaRepository
import com.aitgacem.openmalnet.models.ItemForList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SeasonViewModel @Inject constructor(
    val animeRepository: AnimeRepository,
    val mangaRepository: MangaRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val season = getCurrentSeasonYear()
    val nextSeason = getNextSeason(season)
    val prevSeason = getPreviousSeason(season)

    var position: Int = savedStateHandle["position"] ?: 0

    private val _prevSeasonAnime = MutableLiveData<List<ItemForList>?>()
    val prevSeasonAnime: LiveData<List<ItemForList>?> = _prevSeasonAnime


    private var _seasonAnime = MutableLiveData<List<ItemForList>?>()
    val seasonAnime: LiveData<List<ItemForList>?> = _seasonAnime

    private val _nextSeasonAnime = MutableLiveData<List<ItemForList>?>()
    val nextSeasonAnime: LiveData<List<ItemForList>?> = _nextSeasonAnime

    init {
        viewModelScope.launch {
            _prevSeasonAnime.value = animeRepository.getSeasonAnime(
                prevSeason.year,
                prevSeason.season.query,
                limit = 50
            )
        }
        viewModelScope.launch {
            _seasonAnime.value = animeRepository.getSeasonAnime(
                season.year,
                season.season.query,
                limit = 50
            )
        }
        viewModelScope.launch {
            _nextSeasonAnime.value = animeRepository.getSeasonAnime(
                nextSeason.year,
                nextSeason.season.query,
                limit = 50
            )
        }

    }

    private fun getNextSeason(currentSeasonYear: SeasonYear): SeasonYear {
        val nextSeason = when (currentSeasonYear.season) {
            Season.WINTER -> Season.SPRING
            Season.SPRING -> Season.SUMMER
            Season.SUMMER -> Season.FALL
            Season.FALL -> Season.WINTER
        }
        val nextYear =
            if (nextSeason == Season.WINTER) currentSeasonYear.year + 1 else currentSeasonYear.year
        return SeasonYear(nextSeason, nextYear)
    }

    private fun getPreviousSeason(currentSeasonYear: SeasonYear): SeasonYear {
        val previousSeason = when (currentSeasonYear.season) {
            Season.WINTER -> Season.FALL
            Season.SPRING -> Season.WINTER
            Season.SUMMER -> Season.SPRING
            Season.FALL -> Season.SUMMER
        }
        val previousYear =
            if (previousSeason == Season.WINTER) currentSeasonYear.year - 1 else currentSeasonYear.year
        return SeasonYear(previousSeason, previousYear)
    }

    private fun getCurrentSeasonYear(): SeasonYear {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1

        val season = when (currentMonth) {
            in 1..3 -> Season.WINTER
            in 4..6 -> Season.SPRING
            in 7..9 -> Season.SUMMER
            else -> Season.FALL
        }

        return SeasonYear(season, currentYear)
    }
}
