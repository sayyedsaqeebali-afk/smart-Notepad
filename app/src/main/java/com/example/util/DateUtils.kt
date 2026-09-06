package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

  fun formatTimestamp(timestamp: Long): String {
    if (timestamp <= 0) return ""

    val now = Calendar.getInstance()
    val noteTime = Calendar.getInstance().apply { timeInMillis = timestamp }

    val isSameDay = now.get(Calendar.YEAR) == noteTime.get(Calendar.YEAR) &&
        now.get(Calendar.DAY_OF_YEAR) == noteTime.get(Calendar.DAY_OF_YEAR)

    val isSameYear = now.get(Calendar.YEAR) == noteTime.get(Calendar.YEAR)

    return when {
      // Within the last 60 seconds
      System.currentTimeMillis() - timestamp < 60_000 -> "Just now"

      // Today
      isSameDay -> {
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        "Today, " + timeFormat.format(Date(timestamp))
      }

      // Yesterday
      now.get(Calendar.DAY_OF_YEAR) - noteTime.get(Calendar.DAY_OF_YEAR) == 1 &&
          now.get(Calendar.YEAR) == noteTime.get(Calendar.YEAR) -> {
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        "Yesterday, " + timeFormat.format(Date(timestamp))
      }

      // Same year
      isSameYear -> {
        val dateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
        dateFormat.format(Date(timestamp))
      }

      // Different year
      else -> {
        val fullFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        fullFormat.format(Date(timestamp))
      }
    }
  }
}
