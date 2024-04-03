package com.aitgacem.openmal.ui.fragments.seasonal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
import com.aitgacem.openmal.ui.fragments.details.AnimeDetailFragmentDirections
import com.aitgacem.openmalnet.models.ItemForList
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import java.net.URI

@AndroidEntryPoint
class SeasonContent() : Fragment() {
    constructor(position: Int) : this() {
        this.position = position
    }

    private lateinit var binding: FragmentSeasonalContentBinding
    private val viewModel: SeasonViewModel by viewModels<SeasonViewModel>(extrasProducer = {
        MutableCreationExtras(defaultViewModelCreationExtras).apply {
            set(DEFAULT_ARGS_KEY, bundleOf("position" to position))
        }
    })

    private var position: Int = 0


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

        when (viewModel.position) {
            0 -> {
                viewModel.prevSeasonAnime.observe(viewLifecycleOwner) {
                    (binding.gridRv.adapter as HorizontalListAdapter).submitList(it)
                }
            }

            1 -> {
                viewModel.seasonAnime.observe(viewLifecycleOwner) {
                    (binding.gridRv.adapter as HorizontalListAdapter).submitList(it)
                }
            }

            2 -> {
                viewModel.nextSeasonAnime.observe(viewLifecycleOwner) {
                    (binding.gridRv.adapter as HorizontalListAdapter).submitList(it)
                }
            }
        }
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