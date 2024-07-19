package com.aitgacem.openmal.ui.fragments.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.activity.addCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.aitgacem.openmal.R
import com.aitgacem.openmal.databinding.FragmentSearchBinding
import com.aitgacem.openmal.ui.components.SearchResultsAdapter
import com.aitgacem.openmal.ui.gotoWorkDetail
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.transition.MaterialSharedAxis
import openmal.domain.NetworkResult
import openmal.domain.SortType
import openmal.domain.Work

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by hiltNavGraphViewModels(R.id.main_nav)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, true) {
            resetAndBack()
        }
        val glide = Glide.with(this)
            .applyDefaultRequestOptions(
                RequestOptions().transform(CenterCrop(), RoundedCorners(16))
            )

        val rv = binding.recyclerView
        val adapter = SearchResultsAdapter(glide) { transitionView: View, work: Work ->
            gotoWorkDetail(
                findNavController(), transitionView, work
            )
        }

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
        viewModel.searchResults.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Success -> {
                    if(result.data.isEmpty()){
                        binding.noResultsFoundTextview.visibility = View.VISIBLE
                    } else {
                        binding.noResultsFoundTextview.visibility = View.GONE
                    }
                    adapter.submitList(result.data)
                }
                else -> {

                }
            }
        }
        binding.searchBar.setNavigationOnClickListener(::resetAndBack)
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

        // Sorting and filtering--------
        val sortOrders = arrayOf(
            SortType.DEFAULT to getString(R.string.search_default_order),
            SortType.SCORE to getString(R.string.score),
            SortType.START_DATE to getString(R.string.air_date),
        )
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            sortOrders.map { it.second }).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.spinner.adapter = arrayAdapter

        binding.spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = parent?.getItemAtPosition(position) as? String
                for (sort in sortOrders) {
                    if (sort.second == item) {
                        viewModel.updateSortOrder(sort.first)
                    }
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.updateSortOrder(SortType.DEFAULT)
            }
        }

        binding.filterWorkType.setOnCheckedStateChangeListener { chipGroup, _ ->
            when (chipGroup.checkedChipId) {
                R.id.anime -> viewModel.updateTypeFilter(SearchViewModel.SearchTypeFilter.ANIME)
                R.id.manga -> viewModel.updateTypeFilter(SearchViewModel.SearchTypeFilter.MANGA)
                else -> viewModel.updateTypeFilter(SearchViewModel.SearchTypeFilter.ALL)
            }
        }

        // Suggestions----------
        val suggestionsAdapter = SearchSuggestionsAdapter(Glide.with(this)) { title ->
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
            suggestionsAdapter.submitList(suggestionsList)
        }
    }

    private fun resetAndBack(view: View? = null) {
        viewModel.reset()
        findNavController().popBackStack()
    }

}
