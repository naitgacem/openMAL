package com.aitgacem.openmal.data.network.utils

import android.util.Log

fun onError(e: Exception) {
    Log.e("TAG", "onError: $e")
    e.printStackTrace()
}

