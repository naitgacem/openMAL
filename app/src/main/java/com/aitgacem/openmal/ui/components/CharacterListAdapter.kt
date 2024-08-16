package com.aitgacem.openmal.ui.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aitgacem.openmal.R
import com.aitgacem.openmal.ui.HideProgressBar
import com.bumptech.glide.Glide
import openmal.domain.Character

class CharacterListAdapter(
    private val onItemClicked: (Int) -> Unit
) : ListAdapter<Character, CharacterViewHolder>(CharacterDiff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.character_card, parent, false)
        return CharacterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val character = getItem(position)
        holder.itemView.setOnClickListener {
        }
        holder.imageView.setOnClickListener {
            onItemClicked(character.id)
        }
        holder.nameTextView.text = character.name
        Glide.with(holder.itemView.context)
            .load(character.imageURL)
            .addListener(HideProgressBar(holder.progressBar))
            .into(holder.imageView)
    }
}

class CharacterViewHolder(itemView: View) : ViewHolder(itemView) {
    val nameTextView: TextView = itemView.findViewById(R.id.anime_title)
    val imageView: ImageView = itemView.findViewById(R.id.anime_image)
    val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
}

object CharacterDiff : DiffUtil.ItemCallback<Character>() {
    override fun areContentsTheSame(oldItem: Character, newItem: Character): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areItemsTheSame(oldItem: Character, newItem: Character): Boolean {
        return oldItem == newItem
    }

}