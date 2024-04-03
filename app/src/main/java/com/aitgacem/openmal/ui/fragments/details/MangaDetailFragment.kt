package com.aitgacem.openmal.ui.fragments.details

import android.app.ActionBar
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.aitgacem.openmal.R
import com.aitgacem.openmal.databinding.FragmentMangaDetailBinding
import com.aitgacem.openmal.ui.components.ReadingStatus
import com.aitgacem.openmal.ui.fragments.login.LoginViewModel
import com.aitgacem.openmalnet.models.ItemForList
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import java.net.URI

@AndroidEntryPoint
class MangaDetailFragment : Fragment() {

    private lateinit var binding: FragmentMangaDetailBinding
    private val args: MangaDetailFragmentArgs by navArgs()

    private val vm: MangaDetailViewModel by viewModels(extrasProducer = {
        MutableCreationExtras(defaultViewModelCreationExtras).apply {
            set(DEFAULT_ARGS_KEY, bundleOf("id" to args.id))
        }
    })
    private val loginViewModel: LoginViewModel by hiltNavGraphViewModels(R.id.main_nav)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMangaDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val shouldRefresh =
            findNavController().currentBackStackEntry?.savedStateHandle?.get<Boolean>("REFRESH")
        if (shouldRefresh == true) {
            vm.refresh()
        }
        setupTransition()

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        // Load title and image from the args
        // Glide loads the image from memory so this is fast and reduces wait time
        Glide.with(this)
            .load(args.imageUrl.toString())
            .into(binding.mangaImage)
        binding.topAppBar.title = args.mangaTitle

        // Load Anime info into the header
        val liveDataTextViewPairs = listOf(
            vm.displayTitle to binding.mangaTitle,
            vm.publishingStatus to binding.publishingStatus,
            vm.chapters to binding.chapters,
            vm.type to binding.mangaType,
            vm.ranking to binding.ranked,
            vm.popularity to binding.popularity,
            vm.members to binding.members,
            vm.score to binding.score,
            vm.synopsys to binding.synopsys,
            vm.publishing to binding.publishingDate,
        )

        liveDataTextViewPairs.forEach { (liveData, textView) ->
            observeAndBindText(liveData, textView)
        }

        // Set up the genres chips
        observeAndBind(vm.genres) { genres ->
            for (genre in genres) {
                val chip = Chip(requireContext())
                chip.apply {
                    width = ActionBar.LayoutParams.WRAP_CONTENT
                    height = ActionBar.LayoutParams.WRAP_CONTENT
                    text = genre.name
                    textSize = 12f
                }
                binding.genres.addView(chip)
            }
        }
        // Show a horizontal list of related manga
        observeAndBind(vm.related) { related ->
            // Hide the section if empty
            binding.relatedSect.visibility = if (related.isEmpty()) View.GONE else View.VISIBLE

            for (edge in related) {
                val horizontalChip = LayoutInflater.from(requireContext())
                    .inflate(R.layout.anime_horizontal_card, binding.related, false)
                Glide.with(this).load(edge.node.mainPicture?.medium.toString())
                    .override(200).fitCenter().into(horizontalChip.findViewById(R.id.anime_image))
                horizontalChip.findViewById<TextView>(R.id.anime_title).text =
                    edge.node.originalTitle
                horizontalChip.findViewById<TextView>(R.id.relation_type).text =
                    edge.relationTypeFormatted
                horizontalChip.transitionName = edge.node.originalTitle
                horizontalChip.setOnClickListener {
                    goToMangaDetail(horizontalChip, edge.node)
                }
                binding.related.addView(horizontalChip)
            }
        }
        // Show library info for logged in users
        loginViewModel.isLoggedIn.observe(viewLifecycleOwner) {
            if (it) provideAuthFunctions() else handleNonLogged()
        }

        binding.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.share_btn -> {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Check out ${args.mangaTitle} at ${vm.getMangaUrl()} "
                        )
                        putExtra(Intent.EXTRA_TITLE, args.mangaTitle)
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)

                }

                R.id.lib_btn -> {
                    val sendIntent = Intent(Intent.ACTION_VIEW, Uri.parse(vm.getMangaUrl()))
                    startActivity(sendIntent)
                }

                else -> {

                }
            }
            true
        }
        binding.mangaImage.setOnClickListener{
            vm.refresh() // For testing purposes only
        }

    }

    private fun setupTransition() {
        ViewCompat.setTransitionName(binding.mangaImage, args.mangaTitle)
        val transition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition
    }

    private fun provideAuthFunctions() {
        observeAndBind(vm.givenScore) {
            // Score == 0 means no rating given
            if (it != null && it > 0) {
                binding.starRating.text = "$it/10"
                binding.starRating.icon =
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_star_rate)
            } else {
                binding.starRating.text = "Rate"
            }
        }
        // Show progress in episodes, a max of 0 indicates unknown number of eps
        observeAndBind(vm.readingStatusBundle) {
            val progress = it.first
            val max = it.second
            binding.readProgress.max = if(max > 0) max else progress

            binding.readProgressText.text = "$progress/${if (max > 0) max else "?"}"
            binding.readProgress.setProgress(progress, true)
        }
        observeAndBind(vm.readingStatus) {
            val libraryIcon = binding.topAppBar.menu[0]
            if (it.isNullOrEmpty()) { // not in lib
                binding.libraryStatus.text = "This work is not in your library"
                binding.readProgress.visibility = View.GONE
                binding.readProgressText.visibility = View.GONE
                binding.addToLibBtn.visibility = View.VISIBLE
                libraryIcon.setIcon(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.ic_library_add
                    )
                )
                binding.addToLibBtn.setOnClickListener(::editManga)
                return@observeAndBind
            }
            // Set correct Icon
            libraryIcon.setIcon(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_library_add_check
                )
            )
            // Set the correct state, reading , plan to ...
            for (status in ReadingStatus.entries) {
                if (it == status.name) {
                    binding.libraryStatus.text = status.displayName
                }
            }
            // Show progress bar
            binding.addToLibBtn.visibility = View.GONE
            binding.readProgress.visibility = View.VISIBLE
            binding.readProgressText.visibility = View.VISIBLE
            binding.libraryActionBtn.setOnClickListener(::editManga)

        }
    }

    private fun editManga(view: View?) {
        val info = arrayOf(
            vm.publishingStatus.value ?: "",
            vm.type.value ?: "",
            vm.ranking.value ?: "",
            vm.popularity.value ?: "",
            vm.members.value ?: "",
            vm.score.value ?: "",
        )
        val action = MangaDetailFragmentDirections.editMangaListAction(
            args.imageUrl.toString(),
            info,
            args.id
        )
        findNavController().navigate(action)
    }

    private fun handleNonLogged() {
        binding.libraryStatus.text = "Log in to view your library status"
        binding.addToLibBtn.text = "Log in"
        binding.addToLibBtn.visibility = View.VISIBLE
    }

    private fun <T> observeAndBind(liveData: LiveData<T>, viewUpdater: (T) -> Unit) {
        liveData.observe(viewLifecycleOwner) { data ->
            viewUpdater(data)
        }
    }

    private fun <T : String?> observeAndBindText(liveData: LiveData<T>, textView: TextView) {
        liveData.observe(viewLifecycleOwner) { value ->
            textView.text = value ?: ""
        }
    }

    private fun goToMangaDetail(transitionView: View, it: ItemForList) {
        val action = MangaDetailFragmentDirections.gotoMangaDetail(
            it.id, it.mainPicture?.medium ?: URI(""), it.originalTitle
        )
        findNavController().navigate(
            action, navigatorExtras = FragmentNavigatorExtras(
                transitionView to transitionView.transitionName
            )
        )
    }
}

