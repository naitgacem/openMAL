package com.aitgacem.openmal.ui.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aitgacem.openmal.R
import com.aitgacem.openmalnet.models.ItemForList
import com.bumptech.glide.RequestManager

class SearchResultsAdapter(private val glide: RequestManager, private val onClick: (ItemForList) -> Unit) :
    ListAdapter<ItemForList, SearchResultViewHolder>(ItemDiffCallBack) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.anime_horizontal_card, parent, false)
        return SearchResultViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val anime = getItem(position)
        holder.bind(glide, anime)
    }
}

class SearchResultViewHolder(itemView: View, val onClick: (ItemForList) -> Unit) :
    RecyclerView.ViewHolder(itemView) {
    private val imageView: ImageView = itemView.findViewById(R.id.anime_image)
    private val textView: TextView = itemView.findViewById(R.id.anime_title)
    private val type: TextView = itemView.findViewById(R.id.relation_type)
    private var currentItem: ItemForList? = null

    init {
        itemView.setOnClickListener {
            currentItem?.let {
                onClick(it)
            }
        }
    }

    fun bind(glide: RequestManager, item: ItemForList) {
        currentItem = item
        glide
            .load(item.mainPicture?.medium.toString())
            .into(imageView)
        textView.text = item.displayTitle
        type.textSize = 12f
        type.text = item.synonyms?.getOrElse(0) { item.type }
    }
}