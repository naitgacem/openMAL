package com.aitgacem.openmal.ui.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aitgacem.openmal.R
import com.aitgacem.openmal.databinding.FragmentProfileContentBinding
import com.aitgacem.openmal.ui.components.ProfileMangaListAdapter
import com.aitgacem.openmal.ui.components.ReadingStatus
import com.aitgacem.openmal.ui.fragments.details.MangaDetailFragmentDirections
import com.aitgacem.openmalnet.models.UserMangaListEdge
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import java.net.URI

@AndroidEntryPoint
class ProfileMangaFragment: Fragment() {
    private lateinit var binding: FragmentProfileContentBinding

    private val viewModel: ProfileViewModel by hiltNavGraphViewModels(R.id.main_nav)
    private val map = mutableMapOf<Int, ReadingStatus>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ReadingStatus.entries.forEach{ status ->
            val chip = Chip(requireContext(), null, R.attr.filter_chip_style)
            chip.apply {
                width = ViewGroup.LayoutParams.WRAP_CONTENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                text = status.displayName
                binding.filter.addView(chip)
                chip.setOnClickListener{
                    viewModel.changeFilter(status)
                }
                map[chip.id] = status
            }

        }

        val glide = Glide.with(this)
        val adapter = ProfileMangaListAdapter(glide, ::goToMangaDetail)
        val rv = binding.recyclerview
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(requireContext())

        viewModel.mangaList.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }
        viewModel.mangaHeader.observe(viewLifecycleOwner){
            binding.statsText.text = it
        }

        binding.filterBtn.setOnClickListener{
            val modalBottomSheet = ModalBottomSheet()
            modalBottomSheet.show(parentFragmentManager, ModalBottomSheet.TAG)
        }

    }

    private fun goToMangaDetail(transitionView: View, it: UserMangaListEdge) {
        val action = MangaDetailFragmentDirections.gotoMangaDetail(
            it.node.id, it.node.mainPicture?.medium ?: URI(""), it.node.originalTitle
        )
        findNavController().navigate(
            action, navigatorExtras = FragmentNavigatorExtras(
                transitionView to it.node.originalTitle
            )
        )
    }
}