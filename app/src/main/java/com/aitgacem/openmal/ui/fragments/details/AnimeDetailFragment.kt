package com.aitgacem.openmal.ui.fragments.details

import android.app.ActionBar.LayoutParams
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
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
import com.aitgacem.openmal.databinding.FragmentAnimeDetailBinding
import com.aitgacem.openmal.ui.components.WatchingStatus
import com.aitgacem.openmal.ui.fragments.login.LoginViewModel
import com.aitgacem.openmalnet.models.ItemForList
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import java.net.URI

@AndroidEntryPoint
class AnimeDetailFragment : Fragment() {
    private lateinit var binding: FragmentAnimeDetailBinding
    private val args: AnimeDetailFragmentArgs by navArgs()

    private val vm: AnimeDetailViewModel by viewModels<AnimeDetailViewModel>(extrasProducer = {
        MutableCreationExtras(defaultViewModelCreationExtras).apply {
            set(DEFAULT_ARGS_KEY, bundleOf("id" to args.id))
        }
    })

    private val loginViewModel: LoginViewModel by hiltNavGraphViewModels(R.id.main_nav)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnimeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val shouldRefresh = findNavController().currentBackStackEntry?.savedStateHandle?.get<Boolean>("REFRESH")
        if(shouldRefresh == true){
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
            .into(binding.animeImage)
        binding.topAppBar.title = args.animeTitle


        // Load Anime info into the header
        val liveDataTextViewPairs = listOf(
            vm.displayTitle to binding.animeTitle,
            vm.airingStatus to binding.airingStatus,
            vm.season to binding.season,
            vm.episodes to binding.episodes,
            vm.ratingType to binding.rating,
            vm.raking to binding.ranked,
            vm.popularity to binding.popularity,
            vm.members to binding.members,
            vm.score to binding.score,
            vm.synopsys to binding.synopsys,
            vm.airDate to binding.airDate,
            vm.broadcast to binding.broadcastOn,
            vm.source to binding.source,
            vm.broadcast to binding.broadcastOn,
            vm.studios to binding.studios,
        )
        liveDataTextViewPairs.forEach { (liveData, textView) ->
            observeAndBindText(liveData, textView)
        }

        // Set up the genres chips
        observeAndBind(vm.genres) { genres ->
            for (genre in genres) {
                val chip = Chip(requireContext())
                chip.apply {
                    width = LayoutParams.WRAP_CONTENT
                    height = LayoutParams.WRAP_CONTENT
                    text = genre.name
                    textSize = 12f
                }
                binding.genres.addView(chip)
            }
        }
        // Show a horizontal list of related anime
        observeAndBind(vm.related) { related ->
            // Hide the section if empty
            binding.relatedSect.visibility = if(related.isEmpty()) GONE else VISIBLE

            for (edge in related) {
                val horizontalChip = LayoutInflater.from(requireContext())
                    .inflate(R.layout.anime_horizontal_card, binding.related, false)
                val imageView: ImageView = horizontalChip.findViewById(R.id.anime_image)
                val title: TextView = horizontalChip.findViewById(R.id.anime_title)
                val relationType: TextView = horizontalChip.findViewById(R.id.relation_type)

                Glide.with(this).load(edge.node.mainPicture?.medium.toString())
                    .override(200).fitCenter().into(imageView)
                title.text = edge.node.originalTitle
                relationType.text = edge.relationTypeFormatted

                horizontalChip.transitionName = edge.node.originalTitle // Shared element transition
                horizontalChip.setOnClickListener {
                    goToAnimeDetail(horizontalChip, edge.node)
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
                            "Check out ${args.animeTitle} at ${vm.getAnimeUrl()} "
                        )
                        putExtra(Intent.EXTRA_TITLE, args.animeTitle)
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                }

                R.id.lib_btn -> {
                    val sendIntent = Intent(Intent.ACTION_VIEW, Uri.parse(vm.getAnimeUrl()))
                    startActivity(sendIntent)
                }

                else -> {

                }
            }
            true
        }

        binding.animeImage.setOnClickListener{
            vm.refresh() // For testing purposes only
        }

    }

    private fun setupTransition() {
        ViewCompat.setTransitionName(binding.animeImage, args.animeTitle)
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
        observeAndBind(vm.watchingStatusBundle) {
            val progress = it.first
            val max = it.second
            binding.watchProgress.max = if(max > 0) max else progress

            binding.watchProgressText.text = "$progress/${if (max > 0) max else "?"}"
            binding.watchProgress.setProgress(progress, true)
        }
        observeAndBind(vm.watchingStatus) {
            val libraryIcon = binding.topAppBar.menu[0]
            if (it.isNullOrEmpty()) {
                binding.libraryStatus.text = "This work is not in your library"
                binding.watchProgress.visibility = GONE
                binding.watchProgressText.visibility = GONE
                binding.addToLibBtn.visibility = VISIBLE
                libraryIcon.setIcon(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.ic_library_add
                    )
                )
                binding.addToLibBtn.setOnClickListener(::editAnime)
                return@observeAndBind
            }
            // Set correct Icon
            libraryIcon.setIcon(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_library_add_check
                )
            )
            // Set the correct state, watching , plan to ...
            for (status in WatchingStatus.entries) {
                if (it == status.name) {
                    binding.libraryStatus.text = status.displayName
                }
            }
            // Show progress bar
            binding.addToLibBtn.visibility = GONE
            binding.watchProgress.visibility = VISIBLE
            binding.watchProgressText.visibility = VISIBLE
            binding.libraryActionBtn.setOnClickListener(::editAnime)

        }
    }

    private fun editAnime(view: View?) {
        val info = arrayOf(
            vm.airingStatus.value ?: "",
            vm.season.value ?: "",
            vm.episodes.value ?: "",
            vm.ratingType.value ?: "",
            vm.raking.value ?: "",
            vm.popularity.value ?: "",
            vm.members.value ?: "",
            vm.score.value ?: "",
        )
        val action = AnimeDetailFragmentDirections.editAnimeListAction(
            args.imageUrl.toString(),
            info,
            args.id
        )
        findNavController().navigate(action)
    }

    private fun handleNonLogged() {
        binding.libraryStatus.text = "Log in to view your library status"
        binding.addToLibBtn.text = "Log in"
        binding.addToLibBtn.setOnClickListener{
            loginViewModel.launchBrowserForLogin {
                findNavController().currentBackStackEntry?.savedStateHandle?.set("REFRESH", true)
                startActivity(it)
            }
        }
        binding.addToLibBtn.visibility = VISIBLE
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

    private fun goToAnimeDetail(transitionView: View, it: ItemForList) {
        val action = AnimeDetailFragmentDirections.gotoAnimeDetail(
            it.id, it.mainPicture?.medium ?: URI(""), it.originalTitle
        )
        findNavController().navigate(
            action, navigatorExtras = FragmentNavigatorExtras(
                transitionView to transitionView.transitionName
            )
        )
    }
}

