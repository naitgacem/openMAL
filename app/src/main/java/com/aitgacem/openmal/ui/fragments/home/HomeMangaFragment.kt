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
class HomeMangaFragment : Fragment() {
    private var _binding: FragmentLinearLayoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by hiltNavGraphViewModels(R.id.main_nav)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLinearLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val glide = Glide.with(this)
        val topMangaAdapter = HorizontalListAdapter(glide, ::goToMangaDetail)
        viewModel.topManga.observe(viewLifecycleOwner) {
            topMangaAdapter.submitList(it)
        }
        val topNovelsAdapter = HorizontalListAdapter(glide, ::goToMangaDetail)
        viewModel.topNovels.observe(viewLifecycleOwner) {
            topNovelsAdapter.submitList(it)
        }
        val topOneShotAdapter = HorizontalListAdapter(glide, ::goToMangaDetail)
        viewModel.topOneShots.observe(viewLifecycleOwner) {
            topOneShotAdapter.submitList(it)
        }
        setupSection(
            requireContext(),
            binding.firstTitle,
            binding.firstRv,
            MangaRanking.MANGA.title,
            topMangaAdapter
        )
        setupSection(
            requireContext(),
            binding.secondTitle,
            binding.secondRv,
            MangaRanking.NOVELS.title,
            topNovelsAdapter
        )
        setupSection(
            requireContext(),
            binding.thirdTitle,
            binding.thirdRv,
            MangaRanking.ONESHOTS.title,
            topOneShotAdapter
        )
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