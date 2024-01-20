package com.myungwoo.mp3playerondb.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.myungwoo.mp3playerondb.R
import com.myungwoo.mp3playerondb.data.AsmrData
import com.myungwoo.mp3playerondb.ui.adapter.AsmrWebviewAdapter
import com.myungwoo.mp3playerondb.databinding.ActivityAsmrBinding

class AsmrActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAsmrBinding
    private var dataList = mutableListOf<AsmrData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAsmrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataList.add(AsmrData(getString(R.string.asmr_firewood), R.drawable.iv_firewood_asmr, R.drawable.ic_play_asmr_24, "https://bit.ly/3GJyC4o"))
        dataList.add(AsmrData(getString(R.string.asmr_rain), R.drawable.iv_rain_asmr, R.drawable.ic_play_asmr_24, "https://bit.ly/3KGeP77"))
        dataList.add(AsmrData(getString(R.string.asmr_wave), R.drawable.iv_wave_asmr, R.drawable.ic_play_asmr_24, "https://bit.ly/405FXSZ"))
        dataList.add(AsmrData(getString(R.string.asmr_wind), R.drawable.iv_wind_asmr, R.drawable.ic_play_asmr_24, "https://bit.ly/3zXI8xg"))
        dataList.add(AsmrData(getString(R.string.asmr_fireworks), R.drawable.iv_fireworks_asmr, R.drawable.ic_play_asmr_24, "https://bit.ly/3GL0Fk0"))
        dataList.add(AsmrData(getString(R.string.asmr_ice), R.drawable.ic_ice_asmr, R.drawable.ic_play_asmr_24, "https://bit.ly/3KZYjQO"))
        dataList.add(AsmrData(getString(R.string.asmr_thunder), R.drawable.iv_thunder_asmr, R.drawable.ic_play_asmr_24, "https://bit.ly/3zYTb9c"))
        dataList.add(AsmrData(getString(R.string.asmr_soap), R.drawable.iv_soap_asmr, R.drawable.ic_play_asmr_24, "https://bit.ly/405IQmI"))
        dataList.add(AsmrData(getString(R.string.asmr_forest), R.drawable.iv_forest_asmr, R.drawable.ic_play_asmr_24, "https://bit.ly/3GHVKAo"))
        dataList.add(AsmrData(getString(R.string.asmr_sea), R.drawable.iv_thesea_asmr, R.drawable.ic_play_asmr_24, "https://bit.ly/3ohbUdJ"))

        binding.rvAsmr.adapter = AsmrWebviewAdapter(this, dataList)
        binding.rvAsmr.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )
    }
}