package com.myungwoo.mp3playerondb.recordwave

import android.os.Handler
import android.os.Looper

class Timer(listener: OnTimerTickListener) {
    private var duration = 0L
    private val handler = Handler(Looper.getMainLooper())
    private val runnable: Runnable = object : Runnable {
        override fun run() {
            duration += 40L
            handler.postDelayed(this, 100L)
            listener.onTick(duration)
        }
    }

    fun start() {
        handler.postDelayed(runnable, 100L)
    }

    fun stop() {
        handler.removeCallbacks(runnable)
        duration = 0
    }
}

interface OnTimerTickListener {
    fun onTick(duration: Long)
}