package com.myungwoo.mp3playerondb.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.myungwoo.mp3playerondb.databinding.ActivitySubWebviewBinding

class SubWebViewActivity : AppCompatActivity() {
    lateinit var binding: ActivitySubWebviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.subWebView.webViewClient = WebViewClient()
        binding.subWebView.webChromeClient = WebChromeClient()
        binding.subWebView.settings.javaScriptEnabled = true
        var placeUrl: String = intent.getStringExtra("url") ?: ""
        binding.subWebView.loadUrl(placeUrl)
    }

    override fun onBackPressed() {
        if (binding.subWebView.canGoBack()) binding.subWebView.goBack()
        else super.onBackPressed()
    }
}