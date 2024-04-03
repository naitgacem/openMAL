package com.aitgacem.openmal.ui.fragments.search

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aitgacem.openmal.R
import com.aitgacem.openmal.databinding.FragmentSearchBinding
import com.aitgacem.openmal.ui.components.SearchResultsAdapter
import com.aitgacem.openmal.ui.fragments.details.AnimeDetailFragmentDirections
import com.aitgacem.openmalnet.models.ItemForList
import com.bumptech.glide.Glide
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.net.URI

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by hiltNavGraphViewModels(R.id.main_nav)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val glide = Glide.with(this)
        binding.searchRv.layoutManager = LinearLayoutManager(requireContext())
        binding.searchRv.adapter = SearchResultsAdapter(glide, ::goToAnimeDetail)
        viewModel.searchResults.observe(viewLifecycleOwner) {
            (binding.searchRv.adapter as SearchResultsAdapter).submitList(it)
        }

        binding.searchEditText.addTextChangedListener(onTextChanged = { text, _, _, _ ->
            viewModel.updateSearchTerm(text?.toString())
        })
        binding.searchNormalToolbar.setNavigationOnClickListener {
            hideSoftKeyboard(requireActivity())
            runBlocking { delay(500) }
            findNavController().popBackStack()
        }
        binding.searchEditText.text = SpannableStringBuilder(viewModel.searchTerm.value ?: "")


    }
    private fun hideSoftKeyboard(activity:Activity) {
        if (activity.currentFocus == null){
            return
        }
        val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
    }
    private fun goToAnimeDetail(it: ItemForList){
        val action = AnimeDetailFragmentDirections.gotoAnimeDetail(
            it.id, it.mainPicture?.medium ?: URI(""), it.originalTitle
        )
        findNavController().navigate(action)
    }
}
