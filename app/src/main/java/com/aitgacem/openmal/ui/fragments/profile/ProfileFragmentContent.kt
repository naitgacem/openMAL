package com.aitgacem.openmal.ui.fragments.profile

import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aitgacem.openmal.R
import com.aitgacem.openmal.databinding.FragmentProfileContentBinding
import com.aitgacem.openmal.ui.components.ProfileListAdapter
import com.aitgacem.openmal.ui.gotoWorkDetail
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.color.MaterialColors
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("TAG", "onViewCreated: $this ")
        super.onViewCreated(view, savedInstanceState)
        viewmodel.isRefreshing.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.loadingScreen.visibility = VISIBLE
            } else {
                binding.loadingScreen.visibility = GONE
            }
        }

        val glide = Glide.with(this)
        val adapter = ProfileListAdapter(glide, onClick = { transitionView: View, work: Work ->
            gotoWorkDetail(
                findNavController(), transitionView, work
            )
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
        val headerAdapter = ProfileHeaderAdapter(
            mediaType = viewmodel.mediaType,
            onStateSelected = { listStatus ->
                viewmodel.changeFilter(listStatus)
            },
            activeState = viewmodel.filter.value ?: ListStatus.NON_EXISTENT,
            onFilterButtonClicked = onFilterButtonClicked,
            bindTitleText = { textView ->
                headerLiveData.observe(viewLifecycleOwner) { title: String ->
                    textView.text = title
                }
            }
        )
        val concat = ConcatAdapter(headerAdapter, adapter)
        val rv = binding.recyclerview
        if (requireContext().resources.configuration.uiMode and UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES) {
            // TODO support night theme as well, by choosing an appropriate color which looks good on both night and day modes
            rv.addItemDecoration(ScrimDecoration())
        }
        viewmodel.isRefreshing.observe(viewLifecycleOwner) { isRefresing ->
            binding.swipeRefresh.isRefreshing = isRefresing
            if (!rv.isComputingLayout) {
                rv.invalidateItemDecorations()
            }
        }
        binding.swipeRefresh.setOnRefreshListener {
            viewmodel.refresh()
        }
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = concat
    }

    inner class ScrimDecoration : ItemDecoration() {
        private val paint = Paint().apply {
            color = MaterialColors.getColor(
                requireContext(), com.google.android.material.R.attr.scrimBackground, ""
            )
        }

        private val rect = Rect()

        override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            if (viewmodel.isRefreshing.value == false) {
                return
            }
            val children = parent.children
            for (child in children.drop(1)) {
                val left = child.left
                val right = child.right
                val top = child.top
                val bottom = child.bottom
                rect.set(left, top, right, bottom)
                c.drawRect(rect, paint)
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
        result: NetworkResult<List<Work>>?, adapter: ProfileListAdapter
    ) {
        when (result) {
            is NetworkResult.Success -> {
                adapter.submitList(result.data) {
                }
            }

            else -> {
                Toast.makeText(
                    requireContext(), getString(R.string.check_internet), Toast.LENGTH_SHORT
                ).show()

                binding.refreshLayout.root.visibility = VISIBLE
                binding.refreshLayout.refreshBtn.setOnClickListener {
                    viewmodel.refresh()
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
        private val mapOfChips: MutableMap<Int, ListStatus> = mutableMapOf()

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
                    if (status == activeState) {
                        chip.isChecked = true
                    }
                    filterChipGroup.addView(chip)
                    mapOfChips[chip.id] = status
                }
            }
            filterChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
                if (checkedIds.isEmpty()) {
                    onStateSelected(ListStatus.NON_EXISTENT)
                    return@setOnCheckedStateChangeListener
                }
                check(checkedIds.size == 1) { "More than one chip selected" }
                val checkedStatus = checkNotNull(mapOfChips[checkedIds[0]]) {
                    "Status from chip ID not found"
                }
                onStateSelected(checkedStatus)
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