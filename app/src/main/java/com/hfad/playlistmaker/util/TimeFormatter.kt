package com.hfad.playlistmaker.util

import java.text.SimpleDateFormat
import java.util.Locale

object TimeFormatter {
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    fun formatTime(milliseconds: Long): String {
        return try {
            dateFormat.format(milliseconds)
        } catch (e: Exception) {
            "00:00"
        }
    }

    fun formatTime(milliseconds: Int): String {
        return formatTime(milliseconds.toLong())
    }
}