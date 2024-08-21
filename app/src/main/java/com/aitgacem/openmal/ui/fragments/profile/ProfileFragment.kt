package com.aitgacem.openmal.ui.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.aitgacem.openmal.R
import com.aitgacem.openmal.ui.fragments.login.LoginPromptFragment
import com.aitgacem.openmal.ui.fragments.login.LoginViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import openmal.domain.MediaType

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val loginViewModel: LoginViewModel by hiltNavGraphViewModels(R.id.main_nav)
    private lateinit var tabbedLayoutAdapter: TabbedLayoutAdapter

    class TabbedLayoutAdapter(fragment: Fragment, private val isLoggedIn: Boolean) :
        FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            if (!isLoggedIn) {
                return LoginPromptFragment()
            }
            val fragment = ProfileFragmentContent()
            fragment.arguments = Bundle().apply {
                putSerializable(
                    "media_type",
                    when (position) {
                        0 -> MediaType.ANIME
                        else -> MediaType.MANGA
                    }
                )
            }
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = view.findViewById<ViewPager2>(R.id.pager)
        val menu = view.findViewById<MaterialToolbar>(R.id.toolbar)
        viewPager.isUserInputEnabled = false
        runBlocking {
            val isLoggedIn = loginViewModel.isLoggedInFlow.firstOrNull() == true
            tabbedLayoutAdapter = TabbedLayoutAdapter(this@ProfileFragment, isLoggedIn)
        }
        viewPager.adapter = tabbedLayoutAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Anime"
                1 -> "Manga"
                else -> throw IllegalStateException()
            }
        }.attach()
        menu.setOnMenuItemClickListener { _ ->
            val action = ProfileFragmentDirections.gotoSettings()
            findNavController().navigate(action)
            true
        }


    }

}