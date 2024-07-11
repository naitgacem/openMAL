package com.aitgacem.openmal.ui.fragments.edit

import android.app.ActionBar
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.aitgacem.openmal.R
import com.aitgacem.openmal.databinding.FragmentEditWorkBinding
import com.aitgacem.openmal.ui.convertDateToLong
import com.aitgacem.openmal.ui.convertLongToDate
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import openmal.domain.ListStatus
import openmal.domain.MediaType
import openmal.domain.NetworkResult
import java.text.DateFormat.getDateInstance
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

@AndroidEntryPoint
class EditListFragment : Fragment() {
    private val map = mutableMapOf<ListStatus, Int>()
    private lateinit var binding: FragmentEditWorkBinding
    private val viewModel: EditListViewModel by viewModels(extrasProducer = {
        MutableCreationExtras(defaultViewModelCreationExtras).apply {
            set(
                DEFAULT_ARGS_KEY, bundleOf(
                    "id" to args.id, "type" to args.mediaType
                )
            )
        }
    })
    private val args: EditListFragmentArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditWorkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setTransitionName(binding.workImage, args.title) // For shared element transition
        // attempt to preload the title and image
        Glide.with(this).load(args.imageUrl).into(binding.workImage)
        binding.releaseStatus.text = args.title

        // set appropriate section headers
        when (args.mediaType) {
            MediaType.ANIME -> {
                binding.listStatusTitle.text = resources.getString(R.string.watch_status)
                binding.progressTitle.text = resources.getText(R.string.episodes_watched)
            }

            MediaType.MANGA -> {
                binding.listStatusTitle.text = resources.getString(R.string.reading_status)
                binding.progressTitle.text = resources.getText(R.string.chapters_read)
            }
        }
        // fill the chip group with progress status, watching, reading ...
        ListStatus.entries.filterNot { it == ListStatus.NON_EXISTENT }.forEach { status ->
            val chip = Chip(requireContext()).apply {
                height = ActionBar.LayoutParams.MATCH_PARENT
                width = ActionBar.LayoutParams.WRAP_CONTENT
                isCheckable = true
                text = when (args.mediaType) {
                    MediaType.ANIME -> {
                        resources.getString(
                            when (status) {
                                ListStatus.IN_PROGRESS -> R.string.currently_watching
                                ListStatus.COMPLETED -> R.string.completed
                                ListStatus.ON_HOLD -> R.string.on_hold
                                ListStatus.DROPPED -> R.string.dropped
                                ListStatus.PLAN_TO -> R.string.plan_to_watch
                                ListStatus.NON_EXISTENT -> R.string.unknown
                            }
                        )
                    }

                    MediaType.MANGA -> {
                        resources.getString(
                            when (status) {
                                ListStatus.IN_PROGRESS -> R.string.currently_reading
                                ListStatus.COMPLETED -> R.string.finished_reading
                                ListStatus.ON_HOLD -> R.string.on_hold
                                ListStatus.DROPPED -> R.string.dropped
                                ListStatus.PLAN_TO -> R.string.plan_to_read
                                ListStatus.NON_EXISTENT -> R.string.unknown
                            }
                        )
                    }
                }
                textSize = 12f
            }
            binding.listStatus.addView(chip)
            this.map[status] = chip.id
        }
        binding.listStatus.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val checkedId = checkedIds[0]
                val status = map.filterValues { id ->
                    checkedId == id
                }.keys.firstOrNull()
                if(status != null){
                    viewModel.updateStatus(status)
                    if(status == ListStatus.COMPLETED){
                        viewModel.markProgressFinished()
                    }
                }
                viewModel.updateStatus(status ?: ListStatus.NON_EXISTENT)
            }
        }
        binding.progressEdittext.addTextChangedListener { editable ->
            viewModel.updateProgress(editable.toString())
        }
        // save the original callback of the rating bar
        val callback = binding.rating.ratingBar.onRatingBarChangeListener
        binding.rating.ratingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { ratingBar, givenScore, fromUser ->
                callback.onRatingChanged(ratingBar, givenScore, fromUser)
                if (fromUser) {
                    viewModel.updateGivenScore(givenScore.toInt())
                }
            }
        // start and finish dates
        binding.startDate.setOnClickListener {
            showDatePicker(
                binding.startDate, getString(R.string.select_a_start_date)
            ) { selectedDate ->
                viewModel.updateStartDate(convertLongToDate(selectedDate))
            }
        }
        binding.finishDate.setOnClickListener {
            showDatePicker(
                binding.finishDate, getString(R.string.select_a_start_date)
            ) { selectedDate ->
                viewModel.updateFinishDate(convertLongToDate(selectedDate))
            }
        }
        // notes
        binding.notes.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrBlank()) {
                viewModel.updateNotes(text.toString())
            }
        }
        // load work details, title, image, ..
        viewModel.work.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Success -> {
                    val work = result.data
                    Glide.with(this).load(work.pictureURL).into(binding.workImage)
                    binding.releaseStatus.text = work.userPreferredTitle

                    binding.progressTextLayout.suffixText = String.format(
                        getString(R.string.divider),
                        if (work.numReleases > 0) work.numReleases.toString() else "?"
                    )
                }

                else -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.network_error_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
        // preload user list state if it exists
        viewModel.listStatus.observe(viewLifecycleOwner) { status ->
            if (status == null) return@observe
            binding.listStatus.check(
                map[status.currentStatus] ?: 0
            )
            if (status.progressCount > 0) {
                binding.progressEdittext.text =
                    SpannableStringBuilder(status.progressCount.toString())
            }
            if (status.score > 0) {
                binding.rating.ratingBar.rating = status.score.toFloat()
            }
            val formatter = getDateInstance()
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            if (status.startDate.isNotBlank()) {
                try {
                    binding.startDate.text =
                        formatter.format(Date(convertDateToLong(status.startDate)))
                } catch (_: Exception) {
                }
            }
            if (status.finishDate.isNotBlank()) {
                try {
                    binding.finishDate.text =
                        formatter.format(Date(convertDateToLong(status.finishDate)))
                } catch (_: Exception) {
                }
            }
            binding.notes.editText?.text = SpannableStringBuilder(status.comments)
        }

        binding.saveBtn.setOnClickListener {
            requireActivity().lifecycleScope.launch {
                viewModel.save()
            }
            findNavController().previousBackStackEntry?.savedStateHandle?.set("REFRESH", true)
            findNavController().popBackStack()
        }
        binding.deleteFromListBtn.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.deleting_work), Toast.LENGTH_SHORT)
                .show()
            requireActivity().lifecycleScope.launch {
                val result = viewModel.delete()
                when (result) {
                    is NetworkResult.Success -> Toast.makeText(
                        requireContext(),
                        getString(R.string.work_deleted_from_library),
                        Toast.LENGTH_SHORT
                    ).show()

                    else -> Toast.makeText(
                        requireContext(),
                        getString(R.string.failed_to_remove_work_from_library),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            findNavController().previousBackStackEntry?.savedStateHandle?.set("REFRESH", true)
            findNavController().popBackStack()
        }
    }

    private fun showDatePicker(textView: TextView, title: String, onDateSelected: (Long) -> Unit) {
        val formatter = getDateInstance()
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val selection = try {
            formatter.parse(textView.text.toString())
        } catch (e: Exception) {
            null
        }
        val datePicker =
            MaterialDatePicker.Builder.datePicker().setTitleText(title).setSelection(
                selection?.time
                    ?: Calendar.getInstance().timeInMillis
            )
                .build()

        datePicker.show(getParentFragmentManager(), title)
        datePicker.addOnPositiveButtonClickListener {
            onDateSelected(datePicker.selection ?: return@addOnPositiveButtonClickListener)
            textView.text = formatter.format(
                Date(
                    datePicker.selection ?: return@addOnPositiveButtonClickListener
                )
            )
        }
    }
}
