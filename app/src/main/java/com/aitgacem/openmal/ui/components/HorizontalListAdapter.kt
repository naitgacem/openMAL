package com.aitgacem.openmal.ui.components

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aitgacem.openmal.R
import com.aitgacem.openmalnet.models.ItemForList
import com.bumptech.glide.RequestManager

class HorizontalListAdapter(private val glide: RequestManager, private val onClick: (View, ItemForList) -> Unit = { a, b -> }) :
    ListAdapter<ItemForList, CardViewHolder>(ItemDiffCallBack) {


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
}

object ItemDiffCallBack : DiffUtil.ItemCallback<ItemForList>() {
    override fun areItemsTheSame(oldItem: ItemForList, newItem: ItemForList): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ItemForList, newItem: ItemForList): Boolean {
        return oldItem.id == newItem.id
    }
}

class CardViewHolder(itemView: View, val onClick: (View, ItemForList) -> Unit) :
    RecyclerView.ViewHolder(itemView) {
    private val imageView: ImageView = itemView.findViewById(R.id.anime_image)
    private val textView: TextView = itemView.findViewById(R.id.anime_title)
    private var currentItem: ItemForList? = null

    init {
        itemView.setOnClickListener {
            currentItem?.let {
                onClick(imageView, it)
            }
        }
    }

    fun bind(glide: RequestManager, item: ItemForList) {
        ViewCompat.setTransitionName(imageView, item.originalTitle)
        currentItem = item
        glide
            .load(item.mainPicture?.medium.toString())
            .into(imageView)
        textView.text = item.originalTitle
    }
}