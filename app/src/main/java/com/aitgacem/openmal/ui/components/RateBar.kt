package com.aitgacem.openmal.ui.components

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import com.aitgacem.openmal.R

class RateBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    override fun onFinishInflate() {
        super.onFinishInflate()
        val textView = getChildAt(0) as? TextView
        val ratingBar = getChildAt(1) as? RatingBar
        if (textView != null && ratingBar != null) {
            textView.text = resources.getString(R.string.rate_work)
            ratingBar.onRatingBarChangeListener =
                RatingBar.OnRatingBarChangeListener { _, rating, _ ->
                    textView.text = when (rating.toInt()) {
                        1 -> context.getString(R.string.score_1_10_appalling)
                        2 -> context.getString(R.string.score_2_10_horrible)
                        3 -> context.getString(R.string.score_3_10_very_bad)
                        4 -> context.getString(R.string.score_4_10_bad)
                        5 -> context.getString(R.string.score_5_10_average)
                        6 -> context.getString(R.string.score_6_10_fine)
                        7 -> context.getString(R.string.score_7_10_good)
                        8 -> context.getString(R.string.score_8_10_very_good)
                        9 -> context.getString(R.string.score_9_10_great)
                        10 -> context.getString(R.string.score_10_10_masterpiece)
                        else -> resources.getString(R.string.rate_work)
                    }
                }
        }
    }
}