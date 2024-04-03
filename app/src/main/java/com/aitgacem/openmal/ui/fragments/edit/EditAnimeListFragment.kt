package com.aitgacem.openmal.ui.fragments.edit

import android.app.ActionBar.LayoutParams
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.aitgacem.openmal.databinding.FragmentEditAnimeListBinding
import com.aitgacem.openmal.ui.components.WatchingStatus
import com.aitgacem.openmal.ui.convertDateToLong
import com.aitgacem.openmal.ui.convertLongToDate
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout.END_ICON_CLEAR_TEXT
import com.google.android.material.transition.platform.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

@AndroidEntryPoint
class EditAnimeListFragment : Fragment() {
    private lateinit var binding: FragmentEditAnimeListBinding
    private val args: EditAnimeListFragmentArgs by navArgs()

    private val viewModel: EditAnimeListViewModel by viewModels(extrasProducer = {
        MutableCreationExtras(defaultViewModelCreationExtras).apply {
            set(DEFAULT_ARGS_KEY, bundleOf("id" to args.id))
        }
    })

    private val map = mutableMapOf<Int, WatchingStatus>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditAnimeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        fillHeaderSection()

        // Watching status chips
        WatchingStatus.entries.forEach {
            val chip = Chip(requireContext()).apply {
                height = LayoutParams.MATCH_PARENT
                width = LayoutParams.WRAP_CONTENT
                isCheckable = true
                text = it.displayName
            }
            binding.watchStatus.addView(chip)
            map[chip.id] = it
        }

        binding.watchStatus.setOnCheckedStateChangeListener { chipGroup, checkedList ->
            if (checkedList.isEmpty()) {
                viewModel.updateWatchingStatus(null)
                return@setOnCheckedStateChangeListener
            }
            map[chipGroup.checkedChipId]?.let { viewModel.updateWatchingStatus(it) }
        }

        // Number of episodes watched
        binding.progressEdittext.addTextChangedListener {
            viewModel.updateProgress(it.toString())
        }
        viewModel.maxEps.observe(viewLifecycleOwner) { max ->
            if (max != null && max > 0) {
                binding.progressTextlayout.suffixText = "/${max}"
                binding.progressTextlayout.endIconMode = END_ICON_CLEAR_TEXT
            }
        }

        // Rating of the Anime from 1 to 10 stars
        binding.ratingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { _, givenScore, fromUser ->
                if (fromUser) {
                    viewModel.updateGivenScore(givenScore.toInt())
                }
                binding.ratingText.text = when (givenScore.toInt()) {
                    1 -> "1/10, Appalling"
                    2 -> "2/10, Horrible"
                    3 -> "3/10, Very Bad"
                    4 -> "4/10, Bad"
                    5 -> "5/10, Average"
                    6 -> "6/10, Fine"
                    7 -> "7/10, Good"
                    8 -> "8/10, Very Good"
                    9 -> "9/10, Great"
                    10 -> "10/10, Masterpiece!"
                    else -> ""
                }
            }

        // Start and finish watching dates
        binding.startDate.setOnClickListener {
            showDatePicker(
                binding.startDate, "Select a start date"
            ) { selectedDate ->
                viewModel.updateStartDate(convertLongToDate(selectedDate))
            }
        }
        binding.finishDate.setOnClickListener {
            showDatePicker(
                binding.finishDate, "Select a finish date"
            ) { selectedDate ->
                viewModel.updateFinishDate(convertLongToDate(selectedDate))
            }
        }

        // Comments aka notes
        binding.notes.editText?.doOnTextChanged { text, _, _, _ ->
            if(text != null) {
                viewModel.updateNotes(text.toString())
            }
        }

        // Save and delete buttons
        binding.saveBtn.setOnClickListener {
            requireActivity().lifecycleScope.launch {
                viewModel.save()
            }
            findNavController().previousBackStackEntry?.savedStateHandle?.set("REFRESH", true)
            findNavController().popBackStack()
        }

        binding.deleteFromListBtn.setOnClickListener {
            requireActivity().lifecycleScope.launch {
                viewModel.delete()
            }
            findNavController().previousBackStackEntry?.savedStateHandle?.set("REFRESH", true)
            findNavController().popBackStack()
        }

        // If entry already exists, prefill all the fields
        preFill()
    }

    private fun fillHeaderSection() {
        val airingStatus = args.info[0]
        val season = args.info[1]
        val episodes = args.info[2]
        val rating = args.info[3]
        val rank = args.info[4]
        val popularity = args.info[5]
        val members = args.info[6]
        val score = args.info[7]

        Glide.with(this).load(args.imageUrl).into(binding.animeImage)

        with(binding) {
            this.airingStatus.bind(airingStatus)
            this.season.bind(season)
            this.episodes.bind(episodes)
            this.rating.bind(rating)
            this.ranked.bind(rank)
            this.popularity.bind(popularity)
            this.members.bind(members)
            this.score.bind(score)
        }
    }

    private fun preFill() {
        viewModel.listStatus.observe(viewLifecycleOwner) {
            it?.let {
                binding.progressEdittext.text =
                    SpannableStringBuilder(it.numEpisodesWatched.toString())
                for ((id, status) in map) {
                    if (status.name == it.status) {
                        binding.watchStatus.check(id)
                    }
                }
                binding.ratingBar.rating = it.score.toFloat()
                binding.startDate.text = SpannableStringBuilder(it.startDate ?: "")
                binding.finishDate.text = SpannableStringBuilder(it.finishDate ?: "")
                binding.notes.editText?.text = SpannableStringBuilder(it.comments ?: "")
            }
        }
    }

    private fun showDatePicker(textView: TextView, title: String, onDateSelected: (Long) -> Unit) {
        val selection = convertDateToLong(textView.text.toString())
        val datePicker =
            MaterialDatePicker.Builder.datePicker().setTitleText(title).setSelection(selection)
                .build()

        datePicker.show(getParentFragmentManager(), title)
        datePicker.addOnPositiveButtonClickListener {
            onDateSelected(datePicker.selection ?: return@addOnPositiveButtonClickListener)
            textView.text =
                convertLongToDate(datePicker.selection ?: return@addOnPositiveButtonClickListener)
        }
    }
    private fun TextView.bind(text: String) {
        this.text = text
    }
}


