package com.aitgacem.openmal.ui.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.viewmodel.MutableCreationExtras
import com.aitgacem.openmal.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import openmal.domain.MediaType
import openmal.domain.SortType


@AndroidEntryPoint
class ModalBottomSheet() : BottomSheetDialogFragment() {
    constructor(mediaType: MediaType) : this() {
        this.mediaType = mediaType
    }

    private var mediaType: MediaType = MediaType.ANIME
    val viewModel: ProfileViewModel by viewModels(ownerProducer = {requireParentFragment()}, extrasProducer = {
        MutableCreationExtras(defaultViewModelCreationExtras).apply {
            set(DEFAULT_ARGS_KEY, bundleOf("type" to mediaType))
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.profile_bottomsheet_content, container, false)

    companion object {
        const val TAG = "ProfileBottomSheetContent"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val radioGroup: RadioGroup = view.findViewById(R.id.sort_options)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.filter_title -> {
                    viewModel.changeSorting(SortType.TITLE)
                }

                R.id.filter_score -> {
                    viewModel.changeSorting(SortType.SCORE)
                }

                R.id.filter_updated -> {
                    viewModel.changeSorting(SortType.LAST_UPDATE)
                }

                R.id.filter_start -> {
                    viewModel.changeSorting(SortType.START_DATE)
                }
            }
            viewModel.setLoading(true)
            dismiss()
        }
    }
}
