package com.aitgacem.openmal.ui.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.HorizontalScrollView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aitgacem.openmal.R
import com.aitgacem.openmal.databinding.FragmentProfileContentBinding
import com.aitgacem.openmal.ui.components.ProfileListAdapter
import com.aitgacem.openmal.ui.fragments.details.DetailFragmentDirections
import com.aitgacem.openmal.ui.fragments.login.LoginPromptFragmentDirections
import com.aitgacem.openmal.ui.fragments.login.LoginViewModel
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import openmal.domain.ListStatus
import openmal.domain.MediaType
import openmal.domain.NetworkResult
import openmal.domain.Work

@AndroidEntryPoint
class ProfileFragmentContent() : Fragment() {
    constructor (mediaType: MediaType) : this() {
        this.mediaType = mediaType
    }

    // This variable [mediaType] is a temporary to instantiate the viewModel
    // IGNORE the default value, it's NOT used. This is just to prevent crashes
    // SHOULD NOT BE USED AFTER THE VIEWMODEL IS CREATED!
    private var mediaType: MediaType = MediaType.ANIME

    private lateinit var binding: FragmentProfileContentBinding
    private val viewmodel: ProfileViewModel by viewModels(extrasProducer = {
        MutableCreationExtras(defaultViewModelCreationExtras).apply {
            set(DEFAULT_ARGS_KEY, bundleOf("type" to mediaType))
        }
    })
    private val loginViewModel: LoginViewModel by hiltNavGraphViewModels(R.id.main_nav)
    private val filterMap = mutableMapOf<Int, ListStatus>()

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
        loginViewModel.isLoggedIn.observe(viewLifecycleOwner) { loggedIn ->
            if (!loggedIn) {
                val action = LoginPromptFragmentDirections.gotoLoginPrompt()
                findNavController().navigate(action)
            }
        }
        viewmodel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingScreen.visibility = if (isLoading) VISIBLE else GONE
        }

        val glide = Glide.with(this)
        val adapter = when (viewmodel.mediaType) {
            MediaType.ANIME -> ProfileListAdapter(glide, ::goToAnimeDetail)
            MediaType.MANGA -> ProfileListAdapter(glide, ::goToMangaDetail)
        }
        val rv = binding.recyclerview
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(requireContext())
        viewmodel.workList.observe(viewLifecycleOwner) { result ->
            displayList(result, adapter)
        }

        if (viewmodel.filterChipIds.isNotEmpty()) {
            filterMap.clear()
            filterMap.putAll(viewmodel.filterChipIds)
        } else {
            ListStatus.entries.filterNot { it == ListStatus.NON_EXISTENT }
                .forEach { status ->
                    val id = View.generateViewId()
                    filterMap[id] = status
                }
            viewmodel.filterChipIds.clear()
            viewmodel.filterChipIds.putAll(filterMap)
        }
        restoreChips(filterMap)

        binding.filter.setOnCheckedStateChangeListener { _, integerIds ->
            viewmodel.setLoading(true)
            if (integerIds.isEmpty()) {
                viewmodel.changeFilter(ListStatus.NON_EXISTENT)
            } else if (integerIds.size > 1) {
                throw IllegalStateException("More than one chip selected")
            } else {
                val status =
                    filterMap[integerIds[0]]
                        ?: throw IllegalStateException("Status not found")
                viewmodel.changeFilter(status)
            }

        }

        val animeHeader = MediatorLiveData<String>().apply {
            var filter: ListStatus = ListStatus.NON_EXISTENT
            var stats: Map<ListStatus, Int>? = null
            var totalEpisodes = 0
            addSource(viewmodel.animeStats) {
                stats = it
                value = generateAnimeHeader(filter, stats, totalEpisodes)
            }
            addSource(viewmodel.filter) {
                filter = it
                value = generateAnimeHeader(filter, stats, totalEpisodes)
            }
            addSource(viewmodel.numEpisodesTotal) {
                totalEpisodes = it
                value = generateAnimeHeader(filter, stats, totalEpisodes)
            }
        }
        val mangaHeader = MediatorLiveData<String>().apply {
            var filter = ListStatus.NON_EXISTENT
            addSource(viewmodel.workList) {
                value = generateMangaHeader(filter)
            }
            addSource(viewmodel.filter) { listStatus ->
                filter = listStatus
                value = generateMangaHeader(filter)
            }
        }
        when (viewmodel.mediaType) {
            MediaType.ANIME -> {
                animeHeader.observe(viewLifecycleOwner) {
                    binding.statsText.text = it
                }
            }

            MediaType.MANGA -> {
                mangaHeader.observe(viewLifecycleOwner) {
                    binding.statsText.text = it
                }
            }
        }

        binding.filterBtn.setOnClickListener {
            val modalBottomSheet = ModalBottomSheet(viewmodel.mediaType)
            modalBottomSheet.show(childFragmentManager, ModalBottomSheet.TAG)
        }

    }

    private fun restoreChips(animeFilterMap: MutableMap<Int, ListStatus>) {
        for ((id, listStatus) in animeFilterMap) {
            val chip = Chip(requireContext(), null, R.attr.filter_chip_style)
            chip.id = id
            chip.apply {
                width = LayoutParams.WRAP_CONTENT
                height = LayoutParams.WRAP_CONTENT
                text = getFilterText(viewmodel.mediaType, listStatus)
                binding.filter.addView(chip)
            }
        }
    }

    private fun getFilterText(mediaType: MediaType, status: ListStatus): String {
        return getString(
            when (status) {
                ListStatus.IN_PROGRESS -> when (mediaType) {
                    MediaType.ANIME -> R.string.currently_watching
                    MediaType.MANGA -> R.string.currently_reading
                }

                ListStatus.COMPLETED -> when (mediaType) {
                    MediaType.ANIME -> R.string.finished_watching
                    MediaType.MANGA -> R.string.finished_reading
                }

                ListStatus.PLAN_TO -> when (mediaType) {
                    MediaType.ANIME -> R.string.plan_to_watch
                    MediaType.MANGA -> R.string.plan_to_read
                }

                ListStatus.NON_EXISTENT -> when (mediaType) {
                    MediaType.ANIME -> R.string.all_anime
                    MediaType.MANGA -> R.string.all_manga
                }

                ListStatus.ON_HOLD -> R.string.on_hold
                ListStatus.DROPPED -> R.string.dropped
            }
        )
    }

    private fun displayList(
        result: NetworkResult<List<Work>>?,
        adapter: ProfileListAdapter
    ) {
        when (result) {
            is NetworkResult.Success -> {
                adapter.submitList(result.data) {
                    viewmodel.setLoading(false)
                }
            }

            else -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.check_internet),
                    Toast.LENGTH_SHORT
                ).show()
                viewmodel.setLoading(false)
                binding.refreshLayout.root.visibility = VISIBLE
                binding.refreshLayout.refreshBtn.setOnClickListener {
                    viewmodel.refresh()
                    binding.refreshLayout.root.visibility = GONE
                }
            }
        }
    }

    private fun goToAnimeDetail(transitionView: View, it: Work) {
        val action = DetailFragmentDirections.gotoDetail(
            it.id, MediaType.ANIME, it.pictureURL ?: "", it.originalTitle
        )
        findNavController().navigate(
            action, navigatorExtras = FragmentNavigatorExtras(
                transitionView to it.originalTitle
            )
        )
    }

    private fun generateAnimeHeader(
        filter: ListStatus,
        stats: Map<ListStatus, Int>?,
        numEpisodesTotal: Int
    ): String {
        val type = getString(
            when (filter) {
                ListStatus.IN_PROGRESS -> R.string.currently_watching_anime
                ListStatus.COMPLETED -> R.string.finished_watching_anime
                ListStatus.ON_HOLD -> R.string.on_hold_anime
                ListStatus.DROPPED -> R.string.dropped_anime
                ListStatus.PLAN_TO -> R.string.plan_to_watch_anime
                else -> R.string.all_anime
            }
        )
        if (stats == null) {
            return type
        }
        if (filter == ListStatus.NON_EXISTENT) {
            return String.format(
                getString(R.string.anime_stats_entries_episodes),
                stats[ListStatus.NON_EXISTENT],
                numEpisodesTotal
            )
        }
        return String.format(
            getString(R.string.anime_stats_entries),
            type,
            stats[filter]
        )
    }

    private fun goToMangaDetail(transitionView: View, it: Work) {
        val action = DetailFragmentDirections.gotoDetail(
            it.id,  MediaType.MANGA, it.pictureURL, it.originalTitle
        )
        findNavController().navigate(
            action, navigatorExtras = FragmentNavigatorExtras(
                transitionView to it.originalTitle
            )
        )
    }

    private fun generateMangaHeader(
        filter: ListStatus,
    ): String {
        return getString(
            when (filter) {
                ListStatus.IN_PROGRESS -> R.string.currently_reading_manga
                ListStatus.COMPLETED -> R.string.finished_reading_manga
                ListStatus.ON_HOLD -> R.string.on_hold_manga
                ListStatus.DROPPED -> R.string.dropped_manga
                ListStatus.PLAN_TO -> R.string.plan_to_manga
                else -> R.string.all_manga
            }
        )
    }

    // TODO : enhance this to avoid scrolling unless it is off screen
    private fun scrollToChip(
        horizontalScrollView: HorizontalScrollView,
        chipGroup: ChipGroup,
        chipId: Int
    ) {
        // Find the chip with the given ID
        val chip = chipGroup.findViewById<Chip>(chipId) ?: return

        // Calculate the x-coordinate to scroll to
        var x = 0
        for (i in 0 until chipGroup.indexOfChild(chip)) {
            x += chipGroup.getChildAt(i).width
        }

        // Scroll to the chip
        horizontalScrollView.smoothScrollTo(x, 0)
    }

}