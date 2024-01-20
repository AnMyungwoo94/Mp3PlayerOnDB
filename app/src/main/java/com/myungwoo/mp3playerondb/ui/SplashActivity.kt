package com.myungwoo.mp3playerondb.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.myungwoo.mp3playerondb.databinding.ActivitySplashActivtyBinding

class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashActivtyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashActivtyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            Intent(this, MainActivity::class.java).apply {
                startActivity(this)
                finish()
            }
        }, 1500)
    }
}
