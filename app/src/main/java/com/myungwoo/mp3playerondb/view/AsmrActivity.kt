package com.myungwoo.mp3playerondb.view


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.myungwoo.mp3playerondb.R
import com.myungwoo.mp3playerondb.data.AsmrData
import com.myungwoo.mp3playerondb.adapter.AsmrWebviewAdapter
import com.myungwoo.mp3playerondb.databinding.ActivityAsmrBinding

class AsmrActivity : AppCompatActivity() {
    lateinit var binding : ActivityAsmrBinding
     var dataList: MutableList<AsmrData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAsmrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataList.add(AsmrData("장작소리", R.drawable.firewood, R.drawable.play_asmr_24,"https://bit.ly/3GJyC4o"))
        dataList.add(AsmrData("빗소리",R.drawable.rain,R.drawable.play_asmr_24,"https://bit.ly/3KGeP77"))
        dataList.add(AsmrData("파도소리",R.drawable.wave, R.drawable.play_asmr_24,"https://bit.ly/405FXSZ"))
        dataList.add(AsmrData("바람소리",R.drawable.wind, R.drawable.play_asmr_24,"https://bit.ly/3zXI8xg"))
        dataList.add(AsmrData("폭죽소리",R.drawable.fireworks, R.drawable.play_asmr_24,"https://bit.ly/3GL0Fk0"))
        dataList.add(AsmrData("얼음소리",R.drawable.ice, R.drawable.play_asmr_24,"https://bit.ly/3KZYjQO"))
        dataList.add(AsmrData("번개소리",R.drawable.thunder, R.drawable.play_asmr_24,"https://bit.ly/3zYTb9c"))
        dataList.add(AsmrData("비누소리",R.drawable.soap, R.drawable.play_asmr_24,"https://bit.ly/405IQmI"))
        dataList.add(AsmrData("숲소리",R.drawable.forest, R.drawable.play_asmr_24,"https://bit.ly/3GHVKAo"))
        dataList.add(AsmrData("바다소리",R.drawable.thesea, R.drawable.play_asmr_24,"https://bit.ly/3ohbUdJ"))

        binding.recyclerview.adapter = AsmrWebviewAdapter(this,dataList)
        binding.recyclerview.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false
        )
    }
}