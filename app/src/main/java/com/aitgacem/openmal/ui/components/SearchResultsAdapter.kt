package com.aitgacem.openmal.ui.components

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
import openmal.domain.Anime
import openmal.domain.Manga
import openmal.domain.MediaType
import openmal.domain.Work

class SearchResultsAdapter(
    private val glide: RequestManager,
    private val gotoAnimeDetail: (View, Work) -> Unit,
    private val gotoMangaDetail: (View, Work) -> Unit,
) :
    ListAdapter<Work, SearchResultViewHolder>(WorkDiffCallBack) {

    override fun getItemViewType(position: Int): Int {
        val work = getItem(position)
        return when(work){
            is Anime -> MediaType.ANIME.ordinal
            is Manga -> MediaType.MANGA.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.anime_horizontal_card, parent, false)
        return when(viewType) {
            MediaType.ANIME.ordinal -> SearchResultViewHolder(view, gotoAnimeDetail)
            MediaType.MANGA.ordinal -> SearchResultViewHolder(view, gotoMangaDetail)
            else -> throw IllegalStateException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val anime = getItem(position)
        holder.bind(glide, anime)
    }
}

class SearchResultViewHolder(itemView: View, val gotoDetail: (View, Work) -> Unit) :
    RecyclerView.ViewHolder(itemView) {
    private val imageView: ImageView = itemView.findViewById(R.id.anime_image)
    private val textView: TextView = itemView.findViewById(R.id.anime_title)
    private val type: TextView = itemView.findViewById(R.id.relation_type)
    private var currentItem: Work? = null

    init {
        itemView.setOnClickListener {
            currentItem?.let {work ->
                gotoDetail(imageView, work)
            }
        }
    }

    fun bind(glide: RequestManager, item: Work) {
        ViewCompat.setTransitionName(imageView, item.originalTitle)
        currentItem = item
        glide
            .load(item.pictureURL)
            .into(imageView)
        textView.text = item.userPreferredTitle
        type.textSize = 12f
        type.text = item.mediaType.name
    }
}
