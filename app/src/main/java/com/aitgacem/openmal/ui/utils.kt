package com.aitgacem.openmal.ui

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aitgacem.openmal.R
import com.aitgacem.openmal.ui.components.HorizontalListAdapter
import com.aitgacem.openmal.ui.fragments.details.DetailFragmentDirections
import com.google.android.material.datepicker.MaterialDatePicker
import openmal.domain.Season
import openmal.domain.Work
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun setupSection(
    context: Context,
    titleTextView: TextView,
    recyclerView: RecyclerView,
    title: String,
    adapter: HorizontalListAdapter
) {
    titleTextView.text = title
    recyclerView.layoutManager =
        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    recyclerView.adapter = adapter
}

fun convertLongToDate(longDate: Long?): String {
    if (longDate == null) return ""
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date(longDate))
}

fun convertDateToLong(dateString: String): Long {
    val formats = arrayOf("yyyy-MM-dd", "yyyy-MM", "yyyy")
    for (format in formats) {
        try {
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val date = sdf.parse(dateString)
            if (date != null) {
                return date.time
            }
        } catch (_: Exception) {
        }
    }
    return MaterialDatePicker.todayInUtcMilliseconds()
}

fun gotoWorkDetail(controller: NavController, transitionView: View, work: Work) {
    val action = DetailFragmentDirections.gotoDetail(
        work.id, work.mediaType, work.pictureURL ?: "", work.defaultTitle
    )
    controller.navigate(
        action, navigatorExtras = FragmentNavigatorExtras(
            transitionView to work.defaultTitle
        )
    )
}

fun TypedValue.isColor(): Boolean {
    return (this.type >= TypedValue.TYPE_FIRST_COLOR_INT) && (this.type <= TypedValue.TYPE_LAST_COLOR_INT)
}

fun Pair<Season, Int>.formattedString(context: Context): String {
    return String.format(
        context.getString(R.string.anime_release_season),
        context.getString(
            when (this.first) {
                Season.WINTER -> R.string.winter
                Season.SPRING -> R.string.spring
                Season.SUMMER -> R.string.summer
                Season.FALL -> R.string.fall
            }
        ),
        this.second.toString()
    )
}

fun getScoreColor(context: Context, score: Float): Int {
    return context.getColor(
        when(score.toInt()){
            in 0 until 1 -> R.color.color_1_red
            in 1 until 2 -> R.color.color_2_chili_red
            in 2 until 3 -> R.color.color_3_mahogany
            in 3 until 4 -> R.color.color_4_burnt_orange
            in 4 until 5 -> R.color.color_5_golden_brown
            in 5 until 6 -> R.color.color_6_avocado
            in 6 until 7 -> R.color.color_7_kelly_green
            in 7 until 8 -> R.color.color_8_lime_green
            in 8 until 9 -> R.color.color_9_sgbus_green
            else -> R.color.color_10_green
        }
    )
}