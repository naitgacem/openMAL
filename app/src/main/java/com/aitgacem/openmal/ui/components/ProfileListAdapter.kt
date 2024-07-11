package com.aitgacem.openmal.ui.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aitgacem.openmal.R
import com.bumptech.glide.RequestManager
import openmal.domain.Work

class ProfileListAdapter(private val glide: RequestManager, private val onClick: (View, Work) -> Unit, private val onListChange: (List<Work>, List<Work>) -> Unit ) :
    ListAdapter<Work, ProfileAnimeItemViewHolder>(WorkDiffCallBack) {

    override fun onCurrentListChanged(
        previousList: MutableList<Work>,
        currentList: MutableList<Work>
    ) {
        onListChange(previousList, currentList)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileAnimeItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_list_item, parent, false)
        return ProfileAnimeItemViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ProfileAnimeItemViewHolder, position: Int) {
        val anime = getItem(position)
        holder.bind(glide, anime)
    }
}

class ProfileAnimeItemViewHolder(itemView: View, val onClick: (View, Work) -> Unit) :
    RecyclerView.ViewHolder(itemView) {
    private val imageView: ImageView = itemView.findViewById(R.id.anime_image)
    private val title: TextView = itemView.findViewById(R.id.anime_title)
    private val progress: TextView = itemView.findViewById(R.id.progress)
    private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
    private var currentItem: Work? = null
    private var currentView = itemView
    init {
        itemView.setOnClickListener {
            currentItem?.let {
                onClick(imageView, it)
            }
        }
    }

    fun bind(glide: RequestManager, item: Work) {
        ViewCompat.setTransitionName(imageView, item.defaultTitle)
        currentItem = item
        glide
            .load(item.pictureURL)
            .into(imageView)
        title.text = item.userPreferredTitle

        val userProgress = item.listStatus?.progressCount ?: 0
        val maximum = item.numReleases

        progressBar.max = if(maximum > 0) maximum else userProgress
        progressBar.setProgress(userProgress, true)

        progress.textSize = 12f
        val progressFormat = itemView.context.getString(R.string.progress_format)
        progress.text = String.format(
            progressFormat,
            userProgress.toString(),
            if(maximum > 0) maximum else "?"
        )
    }
}
