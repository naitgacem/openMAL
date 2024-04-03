package com.aitgacem.openmal.ui.fragments.home

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
import com.aitgacem.openmalnet.models.ItemForList
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import java.net.URI

@AndroidEntryPoint
class HomeAnimeFragment : Fragment() {
    private var _binding: FragmentLinearLayoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by hiltNavGraphViewModels(R.id.main_nav)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLinearLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val glide = Glide.with(this)
        val topAiringAdapter = HorizontalListAdapter(glide, ::goToAnimeDetail)
        viewModel.topAiring.observe(viewLifecycleOwner) {
            topAiringAdapter.submitList(it)

        }
        val topAnimeAdapter = HorizontalListAdapter(glide, ::goToAnimeDetail)
        viewModel.topAnime.observe(viewLifecycleOwner) {
            topAnimeAdapter.submitList(it)
        }
        val topSpecialAdapter = HorizontalListAdapter(glide, ::goToAnimeDetail)
        viewModel.topSpecial.observe(viewLifecycleOwner) {
            topSpecialAdapter.submitList(it)
        }
        setupSection(requireContext(),binding.firstTitle, binding.firstRv, AnimeRanking.AIRING.title, topAiringAdapter)
        setupSection(requireContext(),binding.secondTitle, binding.secondRv, AnimeRanking.TV.title, topAnimeAdapter)
        setupSection(requireContext(),binding.thirdTitle, binding.thirdRv, AnimeRanking.SPECIAL.title, topSpecialAdapter)
    }

    private fun goToAnimeDetail(transitionView: View, it: ItemForList) {
        val action = AnimeDetailFragmentDirections.gotoAnimeDetail(
            it.id, it.mainPicture?.medium ?: URI(""), it.originalTitle
        )
        findNavController().navigate(
            action, navigatorExtras = FragmentNavigatorExtras(
                transitionView to it.originalTitle
            )
        )
    }

}