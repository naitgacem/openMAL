package com.aitgacem.openmal.ui.fragments.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.aitgacem.openmal.R
import com.aitgacem.openmal.data.model.AnimeRanking
import com.aitgacem.openmal.databinding.FragmentLinearLayoutBinding
import com.aitgacem.openmal.ui.components.HorizontalListAdapter
import com.aitgacem.openmal.ui.fragments.details.AnimeDetailFragmentDirections
import com.aitgacem.openmal.ui.setupSection
import com.aitgacem.openmal.ui.fragments.login.LoginViewModel
import com.aitgacem.openmalnet.models.ItemForList
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import java.net.URI

@AndroidEntryPoint
class DiscoverAnimeFragment : Fragment() {

    private var _binding: FragmentLinearLayoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DiscoverViewModel by hiltNavGraphViewModels(R.id.main_nav)
    private val loginViewModel: LoginViewModel by hiltNavGraphViewModels(R.id.main_nav)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLinearLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val glide = Glide.with(this)
        val popularAnimeAdapter = HorizontalListAdapter(glide,::goToAnimeDetail)
        viewModel.topPopularAnime.observe(viewLifecycleOwner) {
            popularAnimeAdapter.submitList(it)
        }
        val upcomingAnimeAdapter = HorizontalListAdapter(glide,::goToAnimeDetail)
        viewModel.upcomingAnime.observe(viewLifecycleOwner) {
            upcomingAnimeAdapter.submitList(it)
        }
        loginViewModel.isLoggedIn.observe(viewLifecycleOwner) { isLogged ->
            if (isLogged) {
                val suggestedAnimeAdapter = HorizontalListAdapter(glide,::goToAnimeDetail)
                setupSection(requireContext(),binding.thirdTitle, binding.thirdRv, AnimeRanking.SUGGESTED.title, suggestedAnimeAdapter)
                viewModel.suggestedAnime.observe(viewLifecycleOwner) {
                    suggestedAnimeAdapter.submitList(it)
                }
            } else {
                // Empty the section
                binding.thirdTitle.text = ""
                binding.thirdRv.adapter = HorizontalListAdapter(glide) { _, _ -> }
            }
        }
        setupSection(requireContext(),binding.firstTitle, binding.firstRv, AnimeRanking.BY_POPULARITY.title, popularAnimeAdapter)
        setupSection(requireContext(),binding.secondTitle, binding.secondRv, AnimeRanking.UPCOMING.title, upcomingAnimeAdapter)
    }
    private fun goToAnimeDetail(transitionView: View, it: ItemForList) {
        val action = AnimeDetailFragmentDirections.gotoAnimeDetail(
            it.id, it.mainPicture?.medium ?: URI(""), it.originalTitle
        )
        findNavController().navigate(
            action, navigatorExtras = FragmentNavigatorExtras(
                transitionView to transitionView.transitionName
            )
        )
    }

}