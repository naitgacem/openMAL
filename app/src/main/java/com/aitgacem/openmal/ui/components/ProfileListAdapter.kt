package com.aitgacem.openmal.ui.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aitgacem.openmal.R
import com.aitgacem.openmalnet.models.UserAnimeListEdge
import com.aitgacem.openmalnet.models.UserMangaListEdge
import com.bumptech.glide.RequestManager

class ProfileAnimeListAdapter(private val glide: RequestManager, private val onClick: (View, UserAnimeListEdge) -> Unit) :
    ListAdapter<UserAnimeListEdge, ProfileAnimeItemViewHolder>(ProfileAnimeItemDiffCallBack) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileAnimeItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_list_item, parent, false)
        return ProfileAnimeItemViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ProfileAnimeItemViewHolder, position: Int) {
        val anime = getItem(position)
        holder.bind(glide, anime)
    }
}

class ProfileAnimeItemViewHolder(itemView: View, val onClick: (View, UserAnimeListEdge) -> Unit) :
    RecyclerView.ViewHolder(itemView) {
    private val imageView: ImageView = itemView.findViewById(R.id.anime_image)
    private val title: TextView = itemView.findViewById(R.id.anime_title)
    private val progress: TextView = itemView.findViewById(R.id.progress)
    private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
    private var currentItem: UserAnimeListEdge? = null
    private var currentView = itemView
    init {
        itemView.setOnClickListener {
            currentItem?.let {
                onClick(currentView, it)
            }
        }
    }

    fun bind(glide: RequestManager, item: UserAnimeListEdge) {
        ViewCompat.setTransitionName(currentView, item.node.originalTitle)
        currentItem = item
        glide
            .load(item.node.mainPicture?.medium.toString())
            .into(imageView)
        title.text = item.node.title

        val userProgress = item.listStatus?.numEpisodesWatched ?: 0
        val maximum = item.node.numEpisodes

        progressBar.max = if(maximum > 0) maximum else userProgress
        progressBar.setProgress(userProgress, true)

        progress.textSize = 12f
        progress.text = "${userProgress}/${if(maximum > 0) maximum else "?"}"
    }
}

object ProfileAnimeItemDiffCallBack : DiffUtil.ItemCallback<UserAnimeListEdge>() {
    override fun areItemsTheSame(oldItem: UserAnimeListEdge, newItem: UserAnimeListEdge): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: UserAnimeListEdge, newItem: UserAnimeListEdge): Boolean {
        return oldItem == newItem
    }

}


// -----------------------------------------------------------------------------------------------


class ProfileMangaListAdapter(private val glide: RequestManager, private val onClick: (View, UserMangaListEdge) -> Unit) :
    ListAdapter<UserMangaListEdge, ProfileMangaItemViewHolder>(ProfileMangaItemDiffCallBack) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileMangaItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_list_item, parent, false)
        return ProfileMangaItemViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ProfileMangaItemViewHolder, position: Int) {
        val anime = getItem(position)
        holder.bind(glide, anime)
    }
}

class ProfileMangaItemViewHolder(itemView: View, val onClick: (View, UserMangaListEdge) -> Unit) :
    RecyclerView.ViewHolder(itemView) {
    private val imageView: ImageView = itemView.findViewById(R.id.anime_image)
    private val title: TextView = itemView.findViewById(R.id.anime_title)
    private val progress: TextView = itemView.findViewById(R.id.progress)
    private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
    private var currentItem: UserMangaListEdge? = null
    private var currentView = itemView
    init {
        itemView.setOnClickListener {
            currentItem?.let {
                onClick(currentView, it)
            }
        }
    }

    fun bind(glide: RequestManager, item: UserMangaListEdge) {
        ViewCompat.setTransitionName(currentView, item.node.originalTitle)
        currentItem = item
        glide
            .load(item.node.mainPicture?.medium.toString())
            .into(imageView)
        title.text = item.node.title

        val userProgress = item.listStatus?.numChaptersRead ?: 0
        val maximum = item.node.numChapters

        progressBar.max = if(maximum > 0) maximum else userProgress
        progressBar.setProgress(userProgress, true)

        progress.textSize = 12f
        progress.text = "${userProgress}/${if(maximum > 0) maximum else "?"}"
    }
}

object ProfileMangaItemDiffCallBack : DiffUtil.ItemCallback<UserMangaListEdge>() {
    override fun areItemsTheSame(oldItem: UserMangaListEdge, newItem: UserMangaListEdge): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: UserMangaListEdge, newItem: UserMangaListEdge): Boolean {
        return oldItem == newItem
    }

}