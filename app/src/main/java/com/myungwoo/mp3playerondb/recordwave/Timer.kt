package com.myungwoo.mp3playerondb.recordwave

import android.os.Handler
import android.os.Looper

class Timer(listener: OnTimerTickListener) {
    //쓰레드를 직접 만들어서 사용가능하지만 이번에는 핸들러를 사용하려고 함
    //Runnable은 run함수를 가지고 있는 인터페이스
    private var duration = 0L
    private val handler = Handler(Looper.getMainLooper())
    private val runnable: Runnable = object : Runnable {
        override fun run() {
            duration += 40L
            handler.postDelayed(this, 100L) // 100L가 지날때마다 또 run을 재시작해서 무한으로 돌게함
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

//메인엑티비티가 구현을 받도록, 콜 해주기 (리스너만들기)
interface OnTimerTickListener {
    fun onTick(duration: Long)
}