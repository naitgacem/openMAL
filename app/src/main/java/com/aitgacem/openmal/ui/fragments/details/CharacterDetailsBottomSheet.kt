package com.aitgacem.openmal.ui.fragments.details

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.aitgacem.openmal.R
import com.aitgacem.openmal.databinding.CharacterDetailsBottomsheetContentBinding
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import openmal.domain.Gender
import openmal.domain.NetworkResult

@AndroidEntryPoint
class CharacterDetailsBottomSheet : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "CharacterDetailsBottomSheet"
    }

    private lateinit var binding: CharacterDetailsBottomsheetContentBinding
    val viewModel: CharacterDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CharacterDetailsBottomsheetContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.character.observe(requireParentFragment().viewLifecycleOwner) { result ->
            if (result is NetworkResult.Success) {
                val character = result.data
                binding.characterName.text = character.name
                Glide.with(this).load(character.imageURL)
                    .into(binding.characterImage)
                binding.characterAge.text = String.format(
                    getString(R.string.character_age),
                    character.age ?: getString(R.string.unknown)
                )
                binding.characterGender.text = String.format(
                    getString(R.string.character_gender),
                    getString(
                        when (character.gender) {
                            Gender.Male -> R.string.male
                            Gender.Female -> R.string.female
                            Gender.Unknown -> R.string.unknown
                        }
                    )
                )
                binding.descriptionTextview.text =
                    Html.fromHtml(character.description, Html.FROM_HTML_MODE_LEGACY)

            }

            binding.progressBar.visibility = View.GONE
        }
    }
}