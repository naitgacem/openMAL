package com.aitgacem.openmal.ui.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.aitgacem.openmal.R
import com.aitgacem.openmal.ui.components.AnimeSortType
import com.aitgacem.openmal.ui.components.MangaSortType
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ModalBottomSheet : BottomSheetDialogFragment() {
    val viewModel: ProfileViewModel by hiltNavGraphViewModels(R.id.main_nav)

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
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.filter_title -> {
                    viewModel.changeSorting(AnimeSortType.ANIME_TITLE)
                    viewModel.changeSorting(MangaSortType.MANGA_TITLE)
                }
                R.id.filter_score -> {
                    viewModel.changeSorting(AnimeSortType.LIST_SCORE)
                    viewModel.changeSorting(MangaSortType.LIST_SCORE)
                }
                R.id.filter_updated -> {
                    viewModel.changeSorting(AnimeSortType.LIST_UPDATED)
                    viewModel.changeSorting(MangaSortType.LIST_UPDATED_AT)
                }
                R.id.filter_start -> {
                    viewModel.changeSorting(AnimeSortType.ANIME_START_DATE)
                    viewModel.changeSorting(MangaSortType.MANGA_START_DATE)
                }
            }
            dismiss()
        }
    }
}
