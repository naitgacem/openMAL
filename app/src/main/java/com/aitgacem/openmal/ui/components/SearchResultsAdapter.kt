package com.aitgacem.openmal.ui.components

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aitgacem.openmal.R
import com.bumptech.glide.RequestManager
import com.google.android.material.card.MaterialCardView
import com.google.android.material.color.MaterialColors
import openmal.domain.Anime
import openmal.domain.Manga
import openmal.domain.Season
import openmal.domain.Work

class SearchResultsAdapter(
    private val glide: RequestManager,
    private val gotoWorkDetail: (View, Work) -> Unit,
) :
    ListAdapter<Work, SearchResultViewHolder>(WorkDiffCallBack) {
    private val animeCardBackgroundColor = { context: Context ->
        MaterialColors.getColor(
            context, com.google.android.material.R.attr.colorSecondaryContainer, "Error"
        )
    }
    private val mangaCardBackgroundColor = { context: Context ->
        MaterialColors.getColor(
            context, com.google.android.material.R.attr.colorTertiaryContainer, "Error"
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_result_item, parent, false)
        return SearchResultViewHolder(view, gotoWorkDetail)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val work = getItem(position)
        val backgroundColor = when (work) {
            is Anime -> animeCardBackgroundColor
            is Manga -> mangaCardBackgroundColor
        }
        holder.bind(glide, work, backgroundColor)
    }
}

class SearchResultViewHolder(itemView: View, val gotoDetail: (View, Work) -> Unit) :
    RecyclerView.ViewHolder(itemView) {
    private val imageView: ImageView = itemView.findViewById(R.id.work_image)
    private val titleTextView: TextView = itemView.findViewById(R.id.work_title)
    private val releaseInfo: TextView = itemView.findViewById(R.id.release_date)
    private val mediaType: TextView = itemView.findViewById(R.id.media_type)
    private val mediaTypeCard: MaterialCardView = itemView.findViewById(R.id.media_type_card)
    private val ratingTextView: TextView = itemView.findViewById(R.id.rating_text)
    private var currentItem: Work? = null

    init {
        itemView.setOnClickListener {
            currentItem?.let { work ->
                gotoDetail(imageView, work)
            }
        }
    }

    fun bind(glide: RequestManager, item: Work, backgroundColor: (Context) -> Int) {
        ViewCompat.setTransitionName(imageView, item.defaultTitle)
        currentItem = item
        glide
            .load(item.pictureURL)
            .into(imageView)

        titleTextView.text = item.userPreferredTitle
        if (item.meanScore != null && item.meanScore!! > 0) {
            ratingTextView.text = String.format(
                itemView.context.getString(R.string.rating_star),
                item.meanScore
            )
            ratingTextView.visibility = View.VISIBLE
        } else {
            ratingTextView.visibility = View.GONE
        }
        mediaType.text = itemView.context.getString(
            when (item) {
                is Anime -> R.string.anime
                is Manga -> R.string.manga
            }
        )
        mediaTypeCard.setCardBackgroundColor(backgroundColor(itemView.context))
        if (item is Anime) {
            item.startSeason?.let { pair ->
                releaseInfo.text = String.format(
                    itemView.context.getString(R.string.anime_release_season),
                    itemView.context.getString(
                        when(pair.first){
                            Season.WINTER -> R.string.winter
                            Season.SPRING -> R.string.spring
                            Season.SUMMER -> R.string.summer
                            Season.FALL -> R.string.fall
                        }
                    ),
                    pair.second.toString()
                )
            }
        } else {
            releaseInfo.text = null
        }
    }
}
