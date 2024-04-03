package com.aitgacem.openmal.ui.components

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.aitgacem.openmal.R


class AnimeCard : ConstraintLayout {
    constructor(ctx: Context): super(ctx)

    constructor(ctx: Context, attributeSet: AttributeSet) : super(ctx, attributeSet)

    constructor(ctx: Context, attributeSet: AttributeSet, defStyleAttr: Int): super(ctx, attributeSet, defStyleAttr)

    init {
        inflate(this.context, R.layout.anime_card, this)
    }

    fun setImage(image: Drawable?){
        findViewById<ImageView>(R.id.anime_image).setImageDrawable(image)
    }
    fun setTitle(title: String){
        findViewById<TextView>(R.id.anime_title).text = title
    }
}