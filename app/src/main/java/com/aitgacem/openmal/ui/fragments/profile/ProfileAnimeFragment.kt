package com.aitgacem.openmal.ui.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aitgacem.openmal.R
import com.aitgacem.openmal.databinding.FragmentProfileContentBinding
import com.aitgacem.openmal.ui.components.ProfileAnimeListAdapter
import com.aitgacem.openmal.ui.components.WatchingStatus
import com.aitgacem.openmal.ui.fragments.details.AnimeDetailFragmentDirections
import com.aitgacem.openmal.ui.fragments.login.LoginPromptFragmentDirections
import com.aitgacem.openmal.ui.fragments.login.LoginViewModel
import com.aitgacem.openmalnet.models.UserAnimeListEdge
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import java.net.URI

@AndroidEntryPoint
class ProfileAnimeFragment : Fragment() {
    private lateinit var binding: FragmentProfileContentBinding
    private val viewmodel: ProfileViewModel by hiltNavGraphViewModels(R.id.main_nav)
    private val loginViewModel: LoginViewModel by hiltNavGraphViewModels(R.id.main_nav)
    private val map = mutableMapOf<Int, WatchingStatus>()
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
        loginViewModel.isLoggedIn.observe(viewLifecycleOwner) {
            if (!it) {
                val action = LoginPromptFragmentDirections.gotoLoginPrompt()
                findNavController().navigate(action)
            }
        }

        WatchingStatus.entries.forEach { status ->
            val chip = Chip(requireContext(), null, R.attr.filter_chip_style)
            chip.apply {
                width = LayoutParams.WRAP_CONTENT
                height = LayoutParams.WRAP_CONTENT
                text = status.displayName
                binding.filter.addView(chip)
                chip.setOnClickListener {
                    viewmodel.changeFilter(status)
                }
                map[chip.id] = status
            }
        }

        val glide = Glide.with(this)
        val adapter = ProfileAnimeListAdapter(glide, ::goToAnimeDetail)
        val rv = binding.recyclerview
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(requireContext())

        viewmodel.animeList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewmodel.animeHeader.observe(viewLifecycleOwner) {
            binding.statsText.text = it
        }

        binding.filterBtn.setOnClickListener {
            val modalBottomSheet = ModalBottomSheet()
            modalBottomSheet.show(parentFragmentManager, ModalBottomSheet.TAG)
        }

    }

    private fun goToAnimeDetail(transitionView: View, it: UserAnimeListEdge) {
        val action = AnimeDetailFragmentDirections.gotoAnimeDetail(
            it.node.id, it.node.mainPicture?.medium ?: URI(""), it.node.originalTitle
        )
        findNavController().navigate(
            action, navigatorExtras = FragmentNavigatorExtras(
                transitionView to it.node.originalTitle
            )
        )
    }


}