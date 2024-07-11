package com.aitgacem.openmal.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.aitgacem.openmal.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    class TabbedLayoutAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> HomeAnimeFragment()
                else -> HomeMangaFragment()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        toolbar.title = "Home"
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = view.findViewById<ViewPager2>(R.id.pager)
        viewPager.isUserInputEnabled = false
        val adapter = TabbedLayoutAdapter(this)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.anime)
                1 -> getString(R.string.manga)
                else -> throw IllegalStateException()
            }
        }.attach()
        view.findViewById<Toolbar>(R.id.toolbar).menu[1].setVisible(false)
        view.findViewById<Toolbar>(R.id.toolbar).menu[2].setVisible(false)
        view.findViewById<Toolbar>(R.id.toolbar).menu[0].setIcon(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_search,
                requireContext().theme
            )
        )
        view.findViewById<Toolbar>(R.id.toolbar).menu[0].setOnMenuItemClickListener {
            findNavController().navigate(R.id.search_dest)
            true
        }
    }
}

