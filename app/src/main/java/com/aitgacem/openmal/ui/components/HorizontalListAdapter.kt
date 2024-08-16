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
import com.aitgacem.openmal.ui.HideProgressBar
import com.bumptech.glide.RequestManager
import openmal.domain.Work

class HorizontalListAdapter(private val glide: RequestManager, private val onClick: (View, Work) -> Unit = { a, b -> }) :
    ListAdapter<Work, HorizontalListAdapter.CardViewHolder>(WorkDiffCallBack) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.anime_card, parent, false)
        val lp = view.layoutParams
        val width = (parent.measuredWidth / 3.1).toInt()
        lp.width = width

        return CardViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val anime = getItem(position)
        holder.bind(glide, anime)

    }


    class CardViewHolder(itemView: View, val onClick: (View, Work) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.anime_image)
        private val textView: TextView = itemView.findViewById(R.id.anime_title)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        private var currentItem: Work? = null

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
                .addListener(HideProgressBar(progressBar))
                .into(imageView)
            textView.text = item.userPreferredTitle
        }
    }
}

object WorkDiffCallBack : DiffUtil.ItemCallback<Work>() {
    override fun areItemsTheSame(oldItem: Work, newItem: Work): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Work, newItem: Work): Boolean {
        return oldItem == newItem
    }
}

