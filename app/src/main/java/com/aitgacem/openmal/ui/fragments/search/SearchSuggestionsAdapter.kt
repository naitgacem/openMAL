package com.aitgacem.openmal.ui.fragments.search

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aitgacem.openmal.R
import com.aitgacem.openmal.ui.components.WorkDiffCallBack
import com.bumptech.glide.RequestManager
import openmal.domain.Work

class SearchSuggestionsAdapter(private val glide: RequestManager, private val onClick: (String) -> Unit) :
    ListAdapter<Work, SearchSuggestionViewHolder>(WorkDiffCallBack) {
    override fun onBindViewHolder(holder: SearchSuggestionViewHolder, position: Int) {
        val term = getItem(position)
        holder.bind(term, glide)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchSuggestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_suggestion_item, parent, false)
        return SearchSuggestionViewHolder(view, onClick)
    }
}

class SearchSuggestionViewHolder(itemView: View, val onClick: (String) -> Unit) :
    ViewHolder(itemView) {
    private val imageView = itemView.findViewById<ImageView>(R.id.suggestion_icon)
    private val mainTitle = itemView.findViewById<TextView>(R.id.suggestion_text_main)
    private val altTitle = itemView.findViewById<TextView>(R.id.suggestion_text_alt)
    private val synonymTitle = itemView.findViewById<TextView>(R.id.suggestion_text_synonyms)
    private val suggestionsContainer: LinearLayout = itemView.findViewById(R.id.suggestions)
    private var currentItem: Work? = null
    private lateinit var suggestion: String

    init {
        itemView.setOnClickListener {
            currentItem?.let {
                onClick(suggestion)
            }

        }
    }

    fun bind(suggestion: Work, glide: RequestManager) {
        currentItem = suggestion.also {
            this.suggestion = it.defaultTitle
        }
        val drawable = AppCompatResources.getDrawable(itemView.context, R.drawable.ic_search)
        glide.load(drawable).into(imageView)
        mainTitle.text = suggestion.userPreferredTitle
        if (suggestion.defaultTitle != suggestion.userPreferredTitle) {
            altTitle.text = suggestion.defaultTitle
        } else {
            altTitle.visibility = GONE
        }
        if (suggestion.synonyms.isEmpty()) {
            suggestionsContainer.visibility = GONE
        } else {
            synonymTitle.text = String.format(
                itemView.context.getString(R.string.also_known_as),
                suggestion.synonyms.joinToString()
            )

        }
    }
}
