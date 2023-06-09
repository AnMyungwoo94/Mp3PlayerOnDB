package com.example.mp3playerondb.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.mp3playerondb.activitymainxml.MainActivity
import com.example.mp3playerondb.databinding.ActivitySplashActivtyBinding

class SplashActivty : AppCompatActivity() {
    lateinit var binding: ActivitySplashActivtyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashActivtyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Actionbar 제거
        supportActionBar?.hide()

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(Runnable {
            Intent(this, MainActivity::class.java).apply {
                startActivity(this)
                finish()
            }
        }, 1500) // 3초 후(3000) 스플래시 화면을 닫습니다
    }
}
