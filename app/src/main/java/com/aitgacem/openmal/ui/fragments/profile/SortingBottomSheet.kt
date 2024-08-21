package com.aitgacem.openmal.ui.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.viewModels
import com.aitgacem.openmal.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import openmal.domain.SortType


@AndroidEntryPoint
class SortingBottomSheet : BottomSheetDialogFragment() {
    val viewModel: ProfileViewModel by viewModels(ownerProducer = {requireParentFragment()})

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
                R.id.sort_title -> {
                    viewModel.changeSorting(SortType.TITLE)
                }

                R.id.sort_score -> {
                    viewModel.changeSorting(SortType.SCORE)
                }

                R.id.sort_last_updated -> {
                    viewModel.changeSorting(SortType.LAST_UPDATE)
                }

                R.id.sort_start_date -> {
                    viewModel.changeSorting(SortType.START_DATE)
                }
            }
            dismiss()
        }
    }
}
