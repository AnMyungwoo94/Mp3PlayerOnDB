package com.myungwoo.mp3playerondb.asmractivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.myungwoo.mp3playerondb.R
import com.myungwoo.mp3playerondb.databinding.ActivityWebviewBinding

class WebviewActivity : AppCompatActivity() {
    lateinit var binding: ActivityWebviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //웹뷰를 만들때 필수속성 세가지
        binding.webView.webViewClient = WebViewClient()
        binding.webView.webChromeClient = WebChromeClient()
        binding.webView.settings.javaScriptEnabled= true
        var place_url : String = intent.getStringExtra("url") ?: ""
        binding.webView.loadUrl(place_url)
    }
    override fun onBackPressed() {
        if(binding.webView.canGoBack()) binding.webView.goBack()
        else super.onBackPressed()
    }
}