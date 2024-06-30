package com.aitgacem.openmal.ui.fragments.seasonal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.aitgacem.openmal.databinding.FragmentSeasonalContentBinding
import com.aitgacem.openmal.ui.components.HorizontalListAdapter
import com.aitgacem.openmal.ui.fragments.details.DetailFragmentDirections
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import openmal.domain.MediaType
import openmal.domain.NetworkResult
import openmal.domain.Season
import openmal.domain.Work

@AndroidEntryPoint
class SeasonContent() : Fragment() {
    constructor(season: Pair<Season, Int>) : this() {
        this.season = season
    }

    // This variable [mediaType] is a temporary to instantiate the viewModel
    // IGNORE the default value, it's NOT used. This is just to prevent crashes
    // SHOULD NOT BE USED AFTER THE VIEWMODEL IS CREATED!
    private var season: Pair<Season, Int> = Pair(Season.SPRING, 2024)

    private lateinit var binding: FragmentSeasonalContentBinding
    private val viewModel: SeasonViewModel by viewModels(extrasProducer = {
        MutableCreationExtras(defaultViewModelCreationExtras).apply {
            set(DEFAULT_ARGS_KEY, bundleOf("position" to season))
        }
    })


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeasonalContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val glide = Glide.with(this)
        binding.gridRv.adapter = HorizontalListAdapter(glide, ::goToAnimeDetail)
        binding.gridRv.layoutManager =
            GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)

        viewModel.list.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Success -> {
                    (binding.gridRv.adapter as HorizontalListAdapter).submitList(result.data)
                }
                else -> {
                    binding.refreshLayout.visibility = VISIBLE
                    binding.refreshBtn.setOnClickListener {
                        viewModel.refresh()
                        binding.refreshLayout.visibility = GONE
                    }
                }
            }
        }

    }

    private fun goToAnimeDetail(transitionView: View, it: Work) {
        val action = DetailFragmentDirections.gotoDetail(
            it.id,  MediaType.ANIME, it.pictureURL ?: "", it.defaultTitle
        )
        findNavController().navigate(
            action, navigatorExtras = FragmentNavigatorExtras(
                transitionView to it.defaultTitle
            )
        )
    }


}