package com.aitgacem.openmal.ui.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.aitgacem.openmal.R
import com.aitgacem.openmal.databinding.FragmentPreferencesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreferencesFragment: Fragment() {
    private lateinit var binding: FragmentPreferencesBinding
    private val viewModel by hiltNavGraphViewModels<PreferencesViewModel>(R.id.main_nav)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPreferencesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // NSFW
        viewModel.isNsfwEnabled.observe(viewLifecycleOwner){isEnabled ->
            binding.nsfw.isChecked = isEnabled
        }
        binding.nsfw.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setNsfw(isChecked)
        }

        //
    }
}