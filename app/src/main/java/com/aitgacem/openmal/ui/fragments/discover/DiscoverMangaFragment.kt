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
import com.aitgacem.openmal.data.model.MangaRanking
import com.aitgacem.openmal.databinding.FragmentLinearLayoutBinding
import com.aitgacem.openmal.ui.components.HorizontalListAdapter
import com.aitgacem.openmal.ui.fragments.details.MangaDetailFragmentDirections
import com.aitgacem.openmal.ui.setupSection
import com.aitgacem.openmalnet.models.ItemForList
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import java.net.URI

@AndroidEntryPoint
class DiscoverMangaFragment : Fragment() {

    private var _binding: FragmentLinearLayoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DiscoverViewModel by hiltNavGraphViewModels(R.id.main_nav)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLinearLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val glide = Glide.with(this)
        val popularMangaAdapter = HorizontalListAdapter(glide, ::goToMangaDetail)
        viewModel.topPopularManga.observe(viewLifecycleOwner) {
            popularMangaAdapter.submitList(it)
        }
        val favMangaAdapter = HorizontalListAdapter(glide, ::goToMangaDetail)
        viewModel.mostFavouritedManga.observe(viewLifecycleOwner) {
            favMangaAdapter.submitList(it)
        }
        val topDoujinAdapter = HorizontalListAdapter(glide, ::goToMangaDetail)
        viewModel.topDoujin.observe(viewLifecycleOwner) {
            topDoujinAdapter.submitList(it)
        }

        setupSection(requireContext(),binding.firstTitle, binding.firstRv, MangaRanking.BY_POPULARITY.title, popularMangaAdapter)
        setupSection(requireContext(),binding.secondTitle, binding.secondRv, MangaRanking.FAVORITE.title, favMangaAdapter)
        setupSection(requireContext(),binding.thirdTitle, binding.thirdRv, MangaRanking.DOUJIN.title, topDoujinAdapter)
    }

    private fun goToMangaDetail(transitionView: View, it: ItemForList) {
        val action = MangaDetailFragmentDirections.gotoMangaDetail(
            id = it.id, imageUrl = it.mainPicture?.medium ?: URI(""), mangaTitle = it.originalTitle
        )
        findNavController().navigate(
            action, navigatorExtras = FragmentNavigatorExtras(
                transitionView to it.originalTitle
            )
        )
    }
}