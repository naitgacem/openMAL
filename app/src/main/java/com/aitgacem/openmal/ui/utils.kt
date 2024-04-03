package com.aitgacem.openmal.ui

import android.content.Context
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aitgacem.openmal.ui.components.HorizontalListAdapter
import com.google.android.material.datepicker.MaterialDatePicker
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