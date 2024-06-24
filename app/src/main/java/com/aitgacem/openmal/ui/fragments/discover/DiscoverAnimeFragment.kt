package com.aitgacem.openmal.ui.fragments.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.aitgacem.openmal.R
import com.aitgacem.openmal.databinding.FragmentLinearLayoutBinding
import com.aitgacem.openmal.ui.components.HorizontalListAdapter
import com.aitgacem.openmal.ui.fragments.details.DetailFragmentDirections
import com.aitgacem.openmal.ui.fragments.login.LoginViewModel
import com.aitgacem.openmal.ui.setupSection
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import openmal.domain.ApiError
import openmal.domain.MediaType
import openmal.domain.NetworkResult
import openmal.domain.Work

@AndroidEntryPoint
class DiscoverAnimeFragment : Fragment() {

    private var _binding: FragmentLinearLayoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DiscoverViewModel by hiltNavGraphViewModels(R.id.main_nav)
    private val loginViewModel: LoginViewModel by hiltNavGraphViewModels(R.id.main_nav)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLinearLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val glide = Glide.with(this)
        val popularAnimeAdapter = HorizontalListAdapter(glide, ::goToAnimeDetail)
        viewModel.topPopularAnime.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Success -> popularAnimeAdapter.submitList(result.data)
                is NetworkResult.Error -> onError(result.apiError)
                is NetworkResult.Exception -> onException()
            }
        }
        val upcomingAnimeAdapter = HorizontalListAdapter(glide, ::goToAnimeDetail)
        viewModel.upcomingAnime.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Success -> upcomingAnimeAdapter.submitList(result.data)
                is NetworkResult.Error -> onError(result.apiError)
                is NetworkResult.Exception -> onException()
            }
        }
        loginViewModel.isLoggedIn.observe(viewLifecycleOwner) { isLogged ->
            if (isLogged) {
                val suggestedAnimeAdapter = HorizontalListAdapter(glide, ::goToAnimeDetail)
                setupSection(
                    requireContext(),
                    binding.thirdTitle,
                    binding.thirdRv,
                    getString(R.string.suggested_anime),
                    suggestedAnimeAdapter
                )
                viewModel.suggestedAnime.observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is NetworkResult.Success -> suggestedAnimeAdapter.submitList(result.data)
                        is NetworkResult.Error -> onError(result.apiError)
                        is NetworkResult.Exception -> onException()
                    }
                }
            } else {
                hideSuggested()
            }
        }
        setupSection(
            requireContext(),
            binding.firstTitle,
            binding.firstRv,
            getString(R.string.top_anime_by_popularity),
            popularAnimeAdapter
        )
        setupSection(
            requireContext(),
            binding.secondTitle,
            binding.secondRv,
            getString(R.string.top_upcoming_anime),
            upcomingAnimeAdapter
        )
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

    private fun hideSuggested() {
        binding.thirdTitle.text = ""
        binding.thirdRv.adapter = HorizontalListAdapter(Glide.with(this)) { _, _ -> }
    }
}