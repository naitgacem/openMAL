package com.aitgacem.openmal.ui.fragments.details

import android.content.Intent
import android.content.res.Resources.Theme
import android.graphics.Color
import android.icu.text.DateFormat
import android.icu.text.MessageFormat
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.color
import androidx.core.view.ViewCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.aitgacem.openmal.R
import com.aitgacem.openmal.databinding.FragmentDetailBinding
import com.aitgacem.openmal.ui.components.CharacterListAdapter
import com.aitgacem.openmal.ui.convertDateToLong
import com.aitgacem.openmal.ui.formattedString
import com.aitgacem.openmal.ui.fragments.edit.EditListFragmentDirections
import com.aitgacem.openmal.ui.fragments.edit.EditListViewModel
import com.aitgacem.openmal.ui.fragments.login.LoginViewModel
import com.aitgacem.openmal.ui.getContentRatingColor
import com.aitgacem.openmal.ui.getScoreColor
import com.aitgacem.openmal.ui.gotoWorkDetail
import com.aitgacem.openmal.ui.hideViews
import com.aitgacem.openmal.ui.isColor
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import openmal.domain.Anime
import openmal.domain.ContentRating
import openmal.domain.ListStatus
import openmal.domain.Manga
import openmal.domain.MediaType
import openmal.domain.NetworkResult
import openmal.domain.ReleaseStatus
import openmal.domain.Work
import java.text.DateFormat.getDateInstance

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailBinding
    private val args: DetailFragmentArgs by navArgs()

    private val viewModel: DetailViewModel by viewModels()
    private val editViewModel: EditListViewModel by viewModels()

    private val loginViewModel: LoginViewModel by hiltNavGraphViewModels(R.id.main_nav)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
        }
        enterTransition = MaterialFadeThrough()
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val shouldRefresh =
            findNavController().currentBackStackEntry?.savedStateHandle?.get<Boolean>("REFRESH")
        if (shouldRefresh == true) {
            viewModel.refresh()
        }
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
        viewModel.isRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefresh.isRefreshing = isRefreshing
        }
        ViewCompat.setTransitionName(
            binding.workImage,
            args.workTitle
        ) // For shared element transition
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            val url = String.format(
                getString(
                    when (args.mediaType) {
                        MediaType.ANIME -> R.string.anime_url_template
                        MediaType.MANGA -> R.string.manga_url_template
                    }
                ), args.id
            )
            when (menuItem.itemId) {
                R.id.share_btn -> {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT, "Check out ${args.workTitle} at $url "
                        )
                        putExtra(Intent.EXTRA_TITLE, args.workTitle)
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                }

                R.id.browser_btn -> {
                    val sendIntent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(Uri.parse(url), "text/html")
                    }
                    try {
                        startActivity(sendIntent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.no_browser_found),
                            LENGTH_SHORT
                        ).show()
                    }
                }

                R.id.lib_btn -> {
                    if (viewModel.work.value == null) {
                        return@setOnMenuItemClickListener true
                    }
                    if (viewModel.work.value?.listStatus != null) {
                        requireActivity().lifecycleScope.launch {
                            saveAndToast(
                                preString = getString(R.string.deleting_work),
                                postString = getString(R.string.work_deleted_from_library),
                                errorString = getString(R.string.failed_to_remove_work_from_library),
                            ) {
                                editViewModel.delete()
                            }
                        }
                        return@setOnMenuItemClickListener true
                    }
                    editViewModel.updateStatus(ListStatus.IN_PROGRESS)
                    requireActivity().lifecycleScope.launch {
                        saveAndToast(
                            preString = getString(R.string.adding_work),
                            postString = getString(R.string.work_added_to_library),
                            errorString = getString(R.string.failed_to_add_work_to_library)
                        ) {
                            editViewModel.save()
                        }
                    }
                }

                else -> {
                    return@setOnMenuItemClickListener false
                }
            }
            true
        }
        Glide.with(this).load(args.imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade(0)) // disable transition to avoid visual change
            .into(binding.workImage)
        binding.topAppBar.setNavigationOnClickListener { findNavController().popBackStack() }
        viewModel.state.observe(viewLifecycleOwner) { networkResult ->
            when (networkResult) {
                is NetworkResult.Success -> displayWorkInfo(
                    networkResult.data
                ) { transitionView: View, work: Work ->
                    gotoWorkDetail(
                        findNavController(), transitionView, work
                    )
                }

                else -> {
                    Toast.makeText(
                        requireContext(), getString(R.string.netowork_error_occurred), LENGTH_SHORT
                    ).show()
                    showFloatingButton(
                        getString(R.string.refresh), R.drawable.ic_refresh, requireContext().theme
                    ) {
                        viewModel.refresh()
                        binding.floatingActionButton.hide()
                    }
                }
            }
        }
    }


    private fun displayWorkInfo(work: Work, gotoWorkDetail: (View, Work) -> Unit) {
        // redundantly load image and title for deep links handling
        Glide.with(this).load(work.pictureURL).into(binding.workImage)
        binding.workImage.setOnClickListener {
            val action = DetailFragmentDirections.viewImages(work.pictures.toTypedArray())
            findNavController().navigate(action)
        }

        binding.workTitle.text = work.userPreferredTitle
        val releaseStatusSpannable = SpannableString(
            getString(
                when (work) {
                    is Anime -> {
                        when (work.releaseStatus) {
                            ReleaseStatus.FINISHED -> R.string.anime_finished
                            ReleaseStatus.NOT_YET_RELEASED -> R.string.anime_not_yet_released
                            ReleaseStatus.CURRENTLY_RELEASING -> R.string.anime_currently_airing
                            ReleaseStatus.ON_HIATUS -> R.string.on_hiatus
                            ReleaseStatus.OTHER -> R.string.empty_string
                        }
                    }

                    is Manga -> {
                        when (work.releaseStatus) {
                            ReleaseStatus.FINISHED -> R.string.manga_finished
                            ReleaseStatus.NOT_YET_RELEASED -> R.string.manga_not_yet_released
                            ReleaseStatus.CURRENTLY_RELEASING -> R.string.manga_currently_publishing
                            ReleaseStatus.ON_HIATUS -> R.string.on_hiatus
                            ReleaseStatus.OTHER -> R.string.empty_string
                        }
                    }
                }
            )
        )
        val typedValue = when (work.releaseStatus) {
            ReleaseStatus.CURRENTLY_RELEASING -> resolveColor(R.attr.currently_releasing_color)
            ReleaseStatus.FINISHED -> resolveColor(R.attr.finished_color)
            ReleaseStatus.NOT_YET_RELEASED -> resolveColor(R.attr.not_yet_released_color)
            ReleaseStatus.ON_HIATUS -> resolveColor(R.attr.on_hiatus_color)
            else -> TypedValue()
        }
        if (typedValue.isColor()) {
            releaseStatusSpannable.setSpan(
                ForegroundColorSpan(typedValue.data),
                0,
                releaseStatusSpannable.length,
                SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
        binding.releaseStatus.text = releaseStatusSpannable

        binding.topAppBar.menu[1].setIcon(
            ResourcesCompat.getDrawable(
                resources,
                if (work.listStatus == null) R.drawable.ic_library_add else R.drawable.ic_library_add_check,
                requireContext().theme
            )
        )
        val contentType = resources.getString(
            when (work.contentType) {
                "tv" -> R.string.tv
                "ova" -> R.string.ova
                "movie" -> R.string.movie
                "special" -> R.string.special
                "ona" -> R.string.ona
                "music" -> R.string.music
                "manga" -> R.string.manga
                "novel" -> R.string.novel
                "one_shot" -> R.string.one_shot
                "doujinshi" -> R.string.doujinshi
                "manhwa" -> R.string.manhwa
                "manhua" -> R.string.manhua
                "oel" -> R.string.oel
                else -> R.string.other
            }
        )
        binding.contentType.text = contentType
        work.rank?.let { rankNum ->
            binding.ranked.text = String.format(resources.getString(R.string.ranked_text), rankNum)
        }
        work.popularity?.let { pop ->
            binding.popularity.text =
                String.format(resources.getString(R.string.popularity_text), pop)
        }

        work.members?.let { numMembers ->
            binding.members.text =
                String.format(resources.getString(R.string.members_text), numMembers)
        }
        work.meanScore?.let { givenScore ->
            val prefix = getString(R.string.score_prefix)
            val score = String.format(getString(R.string.score_text), givenScore)
            val spannable = SpannableStringBuilder(prefix)
                .color(getScoreColor(requireContext(), givenScore)) {
                    append(score)
                }
            binding.score.text = spannable
        }
        binding.synopsys.text = work.synopsis
        work.genres.forEach { genre ->
            val chip = Chip(requireContext())
            chip.apply {
                text = genre.name
                textSize = 12f
            }
            binding.genres.addView(chip)
        }
        // characters of the work
        val characterAdapter = CharacterListAdapter { id ->
            val modal = CharacterDetailsBottomSheet()
            modal.arguments = Bundle().apply {
                putInt("id", id)
            }
            modal.show(childFragmentManager, CharacterDetailsBottomSheet.TAG)
        }
        binding.charactersRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.charactersRv.adapter = characterAdapter
        viewModel.characters.observe(viewLifecycleOwner) { result ->
            if (result is NetworkResult.Success && result.data.isNotEmpty()) {
                binding.charactersRv.show()
                characterAdapter.submitList(result.data)
            } else {
                hideViews(binding.charactersRv, binding.charactersDivider, binding.charactersTitleTextview)
            }
        }

        val dateFormat = getDateInstance(DateFormat.MEDIUM)
        binding.releaseDateTxt.text = String.format(
            resources.getString(R.string.start_end_date_format),
            work.startDate?.let { dateFormat.format(convertDateToLong(it)) } ?: "?",
            work.endDate?.let { dateFormat.format(convertDateToLong(it)) } ?: "?",
        )
        if (work.relatedWork.isEmpty()) {
            binding.relatedSect.hide()
        } else {
            work.relatedWork.forEach { pair ->
                val (edge, relation) = pair
                // inflate a horizontal chip and add it to the related works layout
                val horizontalChip = LayoutInflater.from(requireContext())
                    .inflate(R.layout.anime_horizontal_card, binding.relatedWork, false)
                val imageView: ImageView = horizontalChip.findViewById(R.id.anime_image)
                val title: TextView = horizontalChip.findViewById(R.id.anime_title)
                val relationType: TextView = horizontalChip.findViewById(R.id.relation_type)

                Glide.with(this@DetailFragment).load(edge.pictureURL).override(200).fitCenter()
                    .into(imageView)
                title.text = edge.userPreferredTitle
                relationType.text = relation

                horizontalChip.transitionName =
                    edge.userPreferredTitle // Shared element transition
                binding.relatedWork.addView(horizontalChip)
                horizontalChip.setOnClickListener {
                    gotoWorkDetail(it, edge)
                }
            }
        }
        when (work) {
            is Anime -> displayAnimeInfo(work)
            is Manga -> displayMangaInfo(work)
        }
        viewModel.isLogged.observe(viewLifecycleOwner) { isLogged ->
            if (isLogged == null || !isLogged) {
                handleNonLogged()
            } else {
                showListStatus(work)
            }
        }

    }

    private fun displayAnimeInfo(work: Anime) {
        binding.rateTheWork.text = getString(R.string.rate_anime)
        binding.releasePeriod.text = getString(R.string.air_period)
        binding.animeStartSeason.text = work.startSeason?.formattedString(requireContext())
        val contentRatingString = getString(
            when (work.contentRating) {
                ContentRating.G -> R.string.g_rating
                ContentRating.PG -> R.string.pg_rating
                ContentRating.PG_13 -> R.string.pg_13_rating
                ContentRating.R -> R.string.r_rating
                ContentRating.R_PLUS -> R.string.r_plus_rating
                ContentRating.RX -> R.string.rx_rating
                ContentRating.UNKNOWN -> R.string.unknown
            }
        )
        binding.contentRating.text = SpannableStringBuilder().color(
            getContentRatingColor(
                requireContext(),
                work.contentRating
            )
        ) {
            append(contentRatingString)
        }
        binding.numReleases.text = MessageFormat.format(
            getString(R.string.anime_episodes_and_rating), mapOf(
                "episodes" to work.numReleases, "duration" to work.avgEpDuration / 60
            )
        )
        val (day, time) = work.broadcastTime
        val dayOfWeek = getString(
            when (day) {
                "sunday" -> R.string.sunday
                "monday" -> R.string.monday
                "tuesday" -> R.string.tuesday
                "wednesday" -> R.string.wednesday
                "thursday" -> R.string.thursday
                "friday" -> R.string.friday
                "saturday" -> R.string.saturday
                else -> R.string.unknown
            }
        )
        binding.animeBroadcastTxt.text = String.format(
            resources.getString(R.string.broadcast_time_format), dayOfWeek, time
        )
        val source = getString(
            when (work.source) {
                "other" -> R.string.other
                "original" -> R.string.original
                "manga" -> R.string.manga
                "4_koma_manga" -> R.string.four_koma_manga
                "web_manga" -> R.string.web_manga
                "digital_manga" -> R.string.digital_manga
                "novel" -> R.string.novel
                "light_novel" -> R.string.light_novel
                "visual_novel" -> R.string.visual_novel
                "game" -> R.string.game
                "card_game" -> R.string.card_game
                "book" -> R.string.book
                "picture_book" -> R.string.picture_book
                "radio" -> R.string.radio
                "music" -> R.string.music
                else -> R.string.unknown
            }
        )
        binding.relatedSect.text = resources.getString(R.string.related_anime)
        binding.animeSource.text = source
        binding.animeStudios.text =
            work.studios.joinToString { it.name }.takeUnless { it.isBlank() }
                ?: resources.getString(R.string.unknown)

    }

    private fun resolveColor(color: Int): TypedValue {
        val tv = TypedValue()
        requireContext().theme.resolveAttribute(color, tv, true)
        return tv
    }

    private fun displayMangaInfo(work: Manga) {
        with(binding) {
            binding.rateTheWork.text = resources.getString(R.string.rate_manga)
            releasePeriod.text = resources.getString(R.string.publishing_period)
            numReleases.text = String.format(resources.getString(R.string.manga_chapters_count),
                work.numReleases.takeUnless { it == 0 } ?: "?")

        }
        binding.relatedSect.text = resources.getString(R.string.related_manga)
        // Hide unused sections
        binding.animeStartSeason.hide()
        with(binding) {
            hideViews(animeStudios, animeStudiosTitle, animeSource, animeSourceTitle, animeBroadcastTxt, animeBroadcastTitle, contentRating, contentRatingSect)
        }
    }

    private fun showListStatus(work: Work) {
        val listStatus = work.listStatus
        // In here, we assume the user is logged in
        binding.rateBtn.setOnClickListener(::onRateButtonClick)
        binding.addToLibBtn.setOnClickListener(::gotoEditList)
        if (listStatus == null) {
            // Work does not exist in user library
            binding.libraryStatus.text = resources.getString(R.string.work_not_in_lib)
            binding.addToLibBtn.show()
            binding.progressIndicator.hide()
            binding.progressText.hide()
            binding.addToLibBtn.text = resources.getString(R.string.add)
            binding.rateBtn.text = resources.getString(R.string.rate)
            binding.floatingActionButton.hide()
        } else {
            // Show status and progress ..
            binding.addToLibBtn.hide()
            binding.progressIndicator.progress = listStatus.progressCount
            binding.progressIndicator.show()
            binding.progressText.show()
            binding.libraryActionBtn.setOnClickListener(::gotoEditList)
            val score = listStatus.score
            if (score > 0) {
                binding.rateBtn.text = String.format(
                    resources.getString(R.string.score_given_format), score
                )
            } else {
                binding.rateBtn.text = resources.getString(R.string.rate)
            }
            binding.libraryStatus.text = getString(
                when (listStatus.currentStatus) {
                    ListStatus.IN_PROGRESS -> if (listStatus.mediaType == MediaType.ANIME) R.string.currently_watching else R.string.currently_reading
                    ListStatus.COMPLETED -> if (listStatus.mediaType == MediaType.ANIME) R.string.finished_watching else R.string.finished_reading
                    ListStatus.ON_HOLD -> R.string.on_hold
                    ListStatus.DROPPED -> R.string.dropped
                    ListStatus.PLAN_TO -> if (listStatus.mediaType == MediaType.ANIME) R.string.plan_to_watch else R.string.plan_to_read
                    ListStatus.NON_EXISTENT -> R.string.unknown
                }
            )
            binding.progressIndicator.max =
                if (work.numReleases > 0) work.numReleases else listStatus.progressCount
            binding.progressIndicator.show()
            binding.progressIndicator.setProgress(listStatus.progressCount, true)
            binding.progressText.text = String.format(
                resources.getString(R.string.progress_format),
                listStatus.progressCount.toString(),
                if (work.numReleases > 0) work.numReleases.toString() else "?"
            )
            binding.progressText.show()
            showFloatingButton(
                getString(R.string.edit_list), R.drawable.ic_edit, requireContext().theme
            ) { view ->
                gotoEditList(view)
                binding.floatingActionButton.hide()
            }
        }
        binding.rateBtn.show()
    }

    private fun handleNonLogged() {
        binding.libraryStatus.text = getString(R.string.login_to_view)
        binding.rateBtn.text = getString(R.string.login)
        binding.rateBtn.setOnClickListener {
            loginViewModel.launchBrowserForLogin {
                startActivity(it)
            }
        }
        binding.rateBtn.show()
    }

    private fun onRateButtonClick(view: View) {
        val alert =
            MaterialAlertDialogBuilder(requireContext()).setPositiveButton(getString(R.string.confirm)) { dialog, _ ->
                val ratingBar: RatingBar? = (dialog as? AlertDialog)?.findViewById(R.id.rating_bar)
                ratingBar?.let {
                    val score = it.rating
                    editViewModel.updateGivenScore(score.toInt())
                    lifecycleScope.launch(NonCancellable) {
                        editViewModel.save()
                        viewModel.refresh()
                    }
                }
            }.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }.setNeutralButton(getString(R.string.remove)) { dialog, _ ->
                val ratingBar: RatingBar? = (dialog as? AlertDialog)?.findViewById(R.id.rating_bar)
                ratingBar?.let {
                    editViewModel.updateGivenScore(0)
                    lifecycleScope.launch(NonCancellable) {
                        editViewModel.save()
                        viewModel.refresh()
                    }
                }
            }.setView(R.layout.rate_dialog)
        alert.show()
    }

    private fun gotoEditList(view: View?) {
        val action = EditListFragmentDirections.gotoEditList(
            args.id, args.imageUrl ?: "", args.workTitle ?: "", args.mediaType
        )
        findNavController().navigate(
            action, navigatorExtras = FragmentNavigatorExtras(
                binding.workImage as View to (args.workTitle ?: "")
            )
        )
    }

    private suspend fun saveAndToast(
        preString: String,
        postString: String,
        errorString: String,
        execute: suspend () -> NetworkResult<*>
    ) {
        Toast.makeText(
            requireContext(), preString, LENGTH_SHORT
        ).show()
        val result: NetworkResult<*> = execute()
        when (result) {
            is NetworkResult.Success -> Toast.makeText(
                requireContext(),
                postString,
                LENGTH_SHORT
            ).show()

            else -> Toast.makeText(
                requireContext(),
                errorString,
                LENGTH_SHORT
            ).show()
        }
        delay(2000)
        viewModel.refresh()
    }

    private fun showFloatingButton(
        text: String,
        @DrawableRes id: Int,
        theme: Theme,
        onClick: (View) -> Unit = {},
    ) {
        binding.floatingActionButton.icon = ResourcesCompat.getDrawable(
            resources, id, theme
        )
        binding.floatingActionButton.text = text
        binding.floatingActionButton.setOnClickListener(onClick)
        binding.floatingActionButton.show()
    }

    private fun View.hide() {
        this.visibility = GONE
    }

    private fun View.show() {
        this.visibility = VISIBLE
    }

}