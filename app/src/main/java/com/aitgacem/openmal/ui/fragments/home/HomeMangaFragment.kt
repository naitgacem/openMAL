package com.aitgacem.openmal.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.aitgacem.openmal.R
import com.aitgacem.openmal.databinding.FragmentLinearLayoutBinding
import com.aitgacem.openmal.ui.components.HorizontalListAdapter
import com.aitgacem.openmal.ui.gotoWorkDetail
import com.aitgacem.openmal.ui.setupSection
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import openmal.domain.ApiError
import openmal.domain.NetworkResult
import openmal.domain.Work

@AndroidEntryPoint
class HomeMangaFragment : Fragment() {
    private var _binding: FragmentLinearLayoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by hiltNavGraphViewModels(R.id.main_nav)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLinearLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val glide = Glide.with(this)
        val goToMangaDetail = { transitionView: View, work: Work ->
            gotoWorkDetail(
                findNavController(), transitionView, work
            )
        }
        val topMangaAdapter = HorizontalListAdapter(glide, goToMangaDetail)
        viewModel.topManga.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Success -> topMangaAdapter.submitList(result.data)
                is NetworkResult.Error -> onError(result.apiError)
                is NetworkResult.Exception -> onException()
            }
        }
        val topNovelsAdapter = HorizontalListAdapter(glide, goToMangaDetail)
        viewModel.topNovels.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Success -> topNovelsAdapter.submitList(result.data)
                is NetworkResult.Error -> onError(result.apiError)
                is NetworkResult.Exception -> onException()
            }
        }
        val topOneShotAdapter = HorizontalListAdapter(glide, goToMangaDetail)
        viewModel.topOneShots.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Success -> topOneShotAdapter.submitList(result.data)
                is NetworkResult.Error -> onError(result.apiError)
                is NetworkResult.Exception -> onException()
            }
        }
        setupSection(
            requireContext(),
            binding.firstTitle,
            binding.firstRv,
            getString(R.string.top_manga),
            topMangaAdapter
        )
        setupSection(
            requireContext(),
            binding.secondTitle,
            binding.secondRv,
            getString(R.string.top_novels),
            topNovelsAdapter
        )
        setupSection(
            requireContext(),
            binding.thirdTitle,
            binding.thirdRv,
            getString(R.string.top_one_shots),
            topOneShotAdapter
        )
    }


    private fun onError(apiError: ApiError) {
        val message = getString(
            when (apiError) {
                ApiError.BAD_REQUEST -> R.string.bad_request
                ApiError.UNAUTHORIZED -> R.string.unauthorized
                ApiError.FORBIDDEN -> R.string.forbidden
                ApiError.NOT_FOUND -> R.string.not_found
                ApiError.UNKNOWN -> R.string.unknown_error_occurred
            }
        )
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun onException() {
        Toast.makeText(requireContext(), getString(R.string.check_internet), Toast.LENGTH_SHORT)
            .show()
    }
}