package com.aitgacem.openmal.ui.fragments.profile

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.aitgacem.openmal.R
import com.aitgacem.openmal.databinding.FragmentPreferencesBinding
import dagger.hilt.android.AndroidEntryPoint
import openmal.domain.PreferredTitleStyle
import openmal.domain.PreferredTitleStyle.*

@AndroidEntryPoint
class PreferencesFragment : Fragment() {
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
        viewModel.isNsfwEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.nsfw.isChecked = isEnabled
        }
        binding.nsfw.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setNsfw(isChecked)
        }

        // preferred title style
        viewModel.preferredTitleStyle.observe(viewLifecycleOwner) { style ->
            binding.preferredTitleLanguageChoice.text = getString(
                when (style) {
                    PREFER_ENGLISH -> R.string.english
                    PREFER_JAPANESE -> R.string.japanese
                    PREFER_ROMAJI -> R.string.romaji
                    else -> R.string.default_style
                }
            )
        }
        binding.preferredTitleLinearLayout.setOnClickListener { v ->
            val popup = PopupMenu(requireContext(), v)
            popup.menuInflater.inflate(R.menu.preferred_title_style_menu, popup.menu)
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.prefer_default -> viewModel.setPreferredTitleStyle(PREFER_DEFAULT)
                    R.id.prefer_english -> viewModel.setPreferredTitleStyle(PREFER_ENGLISH)
                    R.id.prefer_japanese -> viewModel.setPreferredTitleStyle(PREFER_JAPANESE)
                    R.id.prefer_romaji -> viewModel.setPreferredTitleStyle(PREFER_ROMAJI)
                }
                true
            }
            popup.show()
        }
    }
}