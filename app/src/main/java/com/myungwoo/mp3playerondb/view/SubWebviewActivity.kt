package com.myungwoo.mp3playerondb.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.myungwoo.mp3playerondb.databinding.ActivitySubWebviewBinding
import com.myungwoo.mp3playerondb.databinding.ActivityWebviewBinding

class SubWebviewActivity : AppCompatActivity() {
    lateinit var binding : ActivitySubWebviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.subWebView.webViewClient = WebViewClient()
        binding.subWebView.webChromeClient = WebChromeClient()
        binding.subWebView.settings.javaScriptEnabled= true
        var place_url : String = intent.getStringExtra("url") ?: ""
        binding.subWebView.loadUrl(place_url)
    }

    override fun onBackPressed() {
        if(binding.subWebView.canGoBack()) binding.subWebView.goBack()
        else super.onBackPressed()
    }
}