package com.aitgacem.openmal.ui.fragments.seasonal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.aitgacem.openmal.databinding.FragmentSeasonalContentBinding
import com.aitgacem.openmal.ui.components.HorizontalListAdapter
import com.aitgacem.openmal.ui.gotoWorkDetail
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import openmal.domain.NetworkResult
import openmal.domain.Work

@AndroidEntryPoint
class SeasonContent : Fragment() {

    private lateinit var binding: FragmentSeasonalContentBinding
    private val viewModel: SeasonViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeasonalContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val glide = Glide.with(this)
        binding.gridRv.adapter = HorizontalListAdapter(glide) { transitionView: View, work: Work ->
            gotoWorkDetail(
                findNavController(), transitionView, work
            )
        }
        binding.gridRv.layoutManager =
            GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)

        viewModel.list.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Success -> {
                    (binding.gridRv.adapter as HorizontalListAdapter).submitList(result.data)
                }

                else -> {
                    binding.refreshLayout.visibility = VISIBLE
                    binding.refreshBtn.setOnClickListener {
                        viewModel.refresh()
                        binding.refreshLayout.visibility = GONE
                    }
                }
            }
        }

    }


}