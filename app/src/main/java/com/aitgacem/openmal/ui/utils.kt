package com.aitgacem.openmal.ui

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aitgacem.openmal.ui.components.HorizontalListAdapter
import com.aitgacem.openmal.ui.fragments.details.DetailFragmentDirections
import com.google.android.material.datepicker.MaterialDatePicker
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

fun gotoWorkDetail(controller: NavController, transitionView: View, work: Work){
    val action = DetailFragmentDirections.gotoDetail(
        work.id, work.mediaType, work.pictureURL ?: "", work.defaultTitle
    )
    controller.navigate(
        action, navigatorExtras = FragmentNavigatorExtras(
            transitionView to work.defaultTitle
        )
    )
}