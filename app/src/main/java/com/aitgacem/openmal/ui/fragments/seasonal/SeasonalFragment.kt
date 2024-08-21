package com.aitgacem.openmal.ui.fragments.seasonal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.aitgacem.openmal.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import openmal.domain.Season
import java.util.Calendar

@AndroidEntryPoint
class SeasonalFragment : Fragment() {

    companion object {
        /**
         * The reason we have limited number of seasons is that [TabLayoutMediator] pre-populates
         * the tabs.
         * @see TabLayoutMediator.populateTabsFromPagerAdapter
         */
        const val NUMBER_SEASONS = 26
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    class TabbedLayoutAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = NUMBER_SEASONS

        override fun createFragment(position: Int): Fragment {
            val fragment = SeasonContent()
            fragment.arguments = Bundle().apply {
                putSerializable("season", generatePairFromIndex(position))
            }
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_seasonal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = view.findViewById<ViewPager2>(R.id.pager)
        viewPager.isUserInputEnabled = true

        val adapter = TabbedLayoutAdapter(this)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val season = generatePairFromIndex(position)
            tab.text = "${season.first} ${season.second}"
        }.attach()
        // Jump to the current season.
        viewPager.setCurrentItem(NUMBER_SEASONS - 2, false)
    }
}

/**
 * Hacky way to cycle through the seasons / years going backwards.
 * God forgive me for what I am about to do
 */
fun generatePairFromIndex(index: Int): Pair<Season, Int> {
    val calendar: Calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_MONTH, 15) // center the date
    //compute the displacement in seasons
    val displacement = (SeasonalFragment.NUMBER_SEASONS - 1) - index - 1
    calendar.add(Calendar.MONTH, -displacement * 3)
    // Get the current month and date after adjustment
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH) + 1
    // Find what season is this
    val season = when (currentMonth) {
        in 1..3 -> Season.WINTER
        in 4..6 -> Season.SPRING
        in 7..9 -> Season.SUMMER
        else -> Season.FALL
    }
    return Pair(season, currentYear)
}