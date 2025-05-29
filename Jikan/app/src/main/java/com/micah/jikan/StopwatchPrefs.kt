package com.micah.jikan

import android.content.Context
import android.content.SharedPreferences
import java.sql.Time

class StopwatchPrefs(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("stopwatch_prefs", Context.MODE_PRIVATE)

    fun save(startTime: Long, isRunning: Boolean,pauseOffset: Long) {
        prefs.edit().apply {
            putLong("startTime", startTime)
            putBoolean("isRunning", isRunning)
            putLong("pauseOffset", pauseOffset)
            apply()
        }
    }

    fun getStartTime(): Long = prefs.getLong("startTime", 0L)
    fun isRunning(): Boolean = prefs.getBoolean("isRunning", false)
    fun getPauseOffset(): Long = prefs.getLong("pauseOffset", 0L)

    fun clear() {
        prefs.edit().clear().apply()
    }
}