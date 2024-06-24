package com.aitgacem.openmal.ui.fragments.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.aitgacem.openmal.R
import com.aitgacem.openmal.databinding.FragmentSearchBinding
import com.aitgacem.openmal.ui.components.SearchResultsAdapter
import com.aitgacem.openmal.ui.fragments.details.DetailFragmentDirections
import com.bumptech.glide.Glide
import openmal.domain.MediaType
import openmal.domain.NetworkResult
import openmal.domain.Work

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by hiltNavGraphViewModels(R.id.main_nav)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val glide = Glide.with(this)
        val rv = binding.recyclerView
        val adapter = SearchResultsAdapter(glide, ::goToAnimeDetail, ::goToMangaDetail)

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
        viewModel.searchResults.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Success -> adapter.submitList(result.data)
                else -> {

                }
            }
        }

        binding.searchBar.setNavigationOnClickListener {
            viewModel.reset()
            findNavController().popBackStack()
        }
        binding.searchView.editText.doOnTextChanged { text, _, _, _ ->
            viewModel.updateSearchTerm(text.toString())
        }
        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            val text = binding.searchView.text
            binding.searchBar.setText(text)
            binding.searchView.hide()
            viewModel.doSearch(text.toString())
            false
        }
        val suggestionsAdapter = SearchSuggestionsAdapter(Glide.with(this)){title ->
            binding.searchBar.setText(title)
            binding.searchView.hide()
            viewModel.doSearch(title)
        }
        binding.suggestionsRv.layoutManager = LinearLayoutManager(requireContext())
        binding.suggestionsRv.adapter = suggestionsAdapter
        binding.suggestionsRv.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        viewModel.suggestions.observe(viewLifecycleOwner) { suggestionsList ->
            val suggestions = suggestionsList.map {
                SearchSuggestion(
                    it.userPreferredTitle,
                    it.originalTitle,
                    it.synonyms,
                    R.drawable.ic_search
                )
            }
            suggestionsAdapter.submitList(suggestions)
        }
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
    private fun goToMangaDetail(transitionView: View, it: Work) {
        val action = DetailFragmentDirections.gotoDetail(
            it.id, MediaType.MANGA, it.pictureURL, it.originalTitle
        )
        findNavController().navigate(
            action, navigatorExtras = FragmentNavigatorExtras(
                transitionView to it.originalTitle
            )
        )
    }
}
