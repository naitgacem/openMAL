package com.aitgacem.openmal.ui.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aitgacem.openmal.R
import com.aitgacem.openmal.databinding.FragmentProfileContentBinding
import com.aitgacem.openmal.ui.components.ProfileListAdapter
import com.aitgacem.openmal.ui.fragments.login.LoginPromptFragmentDirections
import com.aitgacem.openmal.ui.fragments.login.LoginViewModel
import com.aitgacem.openmal.ui.gotoWorkDetail
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
        var shouldShowScrim = false
        viewmodel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                if (shouldShowScrim) binding.scrim.visibility = VISIBLE
                binding.loadingScreen.visibility = VISIBLE
            } else {
                binding.scrim.visibility = GONE
                binding.loadingScreen.visibility = GONE
            }
        }

        val glide = Glide.with(this)
        val adapter = ProfileListAdapter(glide, onClick = { transitionView: View, work: Work ->
            gotoWorkDetail(
                findNavController(), transitionView, work
            )
        }, onListChange = { _, currList ->
            shouldShowScrim = currList.isNotEmpty() // Disable the scrim when the list is empty, looks odd
        })

        viewmodel.workList.observe(viewLifecycleOwner) { result ->
            displayList(result, adapter)
        }

        val headerLiveData = when (viewmodel.mediaType) {
            MediaType.ANIME -> MediatorLiveData<String>().apply {
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

            MediaType.MANGA -> MediatorLiveData<String>().apply {
                var filter = ListStatus.NON_EXISTENT
                addSource(viewmodel.workList) {
                    value = generateMangaHeader(filter)
                }
                addSource(viewmodel.filter) { listStatus ->
                    filter = listStatus
                    value = generateMangaHeader(filter)
                }
            }
        }

        val onFilterButtonClicked = { _: View ->
            val modalBottomSheet = ModalBottomSheet(viewmodel.mediaType)
            modalBottomSheet.show(childFragmentManager, ModalBottomSheet.TAG)
        }
        val headerAdapter = ProfileHeaderAdapter(mediaType = viewmodel.mediaType,
            onStateSelected = { listStatus ->
                viewmodel.changeFilter(listStatus)
                viewmodel.setLoading(true)
            },
            activeState = viewmodel.filter.value ?: ListStatus.NON_EXISTENT,
            onFilterButtonClicked = onFilterButtonClicked,
            bindTitleText = { textView ->
                headerLiveData.observe(viewLifecycleOwner) { title: String ->
                    textView.text = title
                }
            })
        val concat = ConcatAdapter(headerAdapter, adapter)
        val rv = binding.recyclerview
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = concat
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
        result: NetworkResult<List<Work>>?, adapter: ProfileListAdapter
    ) {
        when (result) {
            is NetworkResult.Success -> {
                adapter.submitList(result.data) {
                    viewmodel.setLoading(false)
                }
            }

            else -> {
                Toast.makeText(
                    requireContext(), getString(R.string.check_internet), Toast.LENGTH_SHORT
                ).show()
                viewmodel.setLoading(false)
                binding.refreshLayout.root.visibility = VISIBLE
                binding.refreshLayout.refreshBtn.setOnClickListener {
                    viewmodel.refresh()
                    viewmodel.setLoading(true)
                    binding.refreshLayout.root.visibility = GONE
                }
            }
        }
    }


    private fun generateAnimeHeader(
        filter: ListStatus, stats: Map<ListStatus, Int>?, numEpisodesTotal: Int
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
            getString(R.string.anime_stats_entries), type, stats[filter]
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

    inner class ProfileHeaderAdapter(
        private val mediaType: MediaType,
        private val onStateSelected: (ListStatus) -> Unit,
        private val activeState: ListStatus,
        private val onFilterButtonClicked: (View) -> Unit,
        private val bindTitleText: (TextView) -> Unit,
    ) : RecyclerView.Adapter<ProfileHeaderAdapter.ProfileHeaderViewHolder>() {
        inner class ProfileHeaderViewHolder(view: View) : ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileHeaderViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.profile_screen_header, parent, false)
            val filterChipGroup: ChipGroup = view.findViewById(R.id.filter)
            val statsTextView: TextView = view.findViewById(R.id.stats_text)
            val filterBtn: MaterialButton = view.findViewById(R.id.filter_btn)
            for (status in ListStatus.entries.filterNot { it == ListStatus.NON_EXISTENT }) {
                val chip = Chip(parent.context, null, R.attr.filter_chip_style)
                chip.apply {
                    width = LayoutParams.WRAP_CONTENT
                    height = LayoutParams.WRAP_CONTENT
                    text = getFilterText(mediaType, status)
                    setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            onStateSelected(status)
                        } else {
                            onStateSelected(ListStatus.NON_EXISTENT)
                        }
                    }
                    if (status == activeState) {
                        chip.isChecked = true
                    }
                    filterChipGroup.addView(chip)
                }
            }

            filterBtn.setOnClickListener(onFilterButtonClicked)
            bindTitleText(statsTextView)
            return ProfileHeaderViewHolder(view)
        }

        override fun onBindViewHolder(holder: ProfileHeaderViewHolder, position: Int) {
            // nothing
        }

        override fun getItemCount(): Int {
            return 1
        }
    }
}