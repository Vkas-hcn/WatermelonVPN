package com.jumping.hamster.playing.hockey.watermelon.utils

import android.os.Handler
import android.os.Looper

object GlobalTimer {

    private var startTime: Long = 0L
    private var timerRunning = false
    private var timerHandler: Handler = Handler(Looper.getMainLooper())
    private var onTimeUpdateListener: ((String) -> Unit)? = null

    private val timerRunnable: Runnable = object : Runnable {
        override fun run() {
            if (timerRunning) {
                val elapsedTime = System.currentTimeMillis() - startTime
                val formattedTime = formatElapsedTime(elapsedTime)
                onTimeUpdateListener?.invoke(formattedTime)
                timerHandler.postDelayed(this, 1000) // 每秒更新一次
            }
        }
    }

    fun startTimer() {
        if (!timerRunning) {
            startTime = System.currentTimeMillis()
            timerRunning = true
            timerHandler.post(timerRunnable)
        }
    }

    fun stopTimer() {
        timerRunning = false
        onTimeUpdateListener?.invoke("00:00:00")
        timerHandler.removeCallbacks(timerRunnable)
    }

    fun setOnTimeUpdateListener(listener: (String) -> Unit) {
        onTimeUpdateListener = listener
    }

    private fun formatElapsedTime(elapsedTime: Long): String {
        val totalSeconds = elapsedTime / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}
