package com.myungwoo.mp3playerondb.record

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.red
import java.time.Duration

class WaveFormView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val ampList = mutableListOf<Float>() //addAmplitude가 저장이 될 때 원시 데이터를 저장
    private val rectList = mutableListOf<RectF>() //그려질 데이터를 담는 리스트(파동을 담을 리스트)
    private val rectWidth = 15f
    private var tick = 0

//    처음에 xml에 띄우기 위해서 선언
//    val rectF = RectF(20f, 30f, 20f + 30f, 30f + 60f)
private val redPaint = Paint().apply {
    color = Color.RED
}


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //1. 녹음 상자를 만들어준다.
        //2. 진폭을 RecordActivity에서 가져온다.

        for (rectF in rectList) {
            canvas?.drawRect(rectF, redPaint)
        }
//        처음에 xml에 띄우기 위해서 선언
//        canvas?.drawRect(rectF, redPaint)
    }

    fun addAmplitude(maxAmplitude: Float) {
        val maxAmplitude = (maxAmplitude / Short.MAX_VALUE) * this.height * 0.8f

        ampList.add(maxAmplitude)
        rectList.clear() // 이유는 ampList를 통해서 rectList를 구성해야 하기 때문 왜? 옆으로 한칸씩 이동해야해서

        val maxRect = (this.width / rectWidth).toInt() //몇개의 rectF이 들어갈 수 있는지
        val amps = ampList.takeLast(maxRect) // 화면 사이즈에 맞는 몇개만 가져옴

        //withIndex = 인덱스와 amp값을 동시에 받아 올 수 있음
        for ((i, amp) in amps.withIndex()) {
            val rectF = RectF()
            rectF.top = (this.height / 2) - amp / 2 - 3f //위로절반 아래로 절반 가운데
            rectF.bottom = rectF.top + amp + 3f
            rectF.left = i * rectWidth
            rectF.right = rectF.left + rectWidth - 5f // 여백을 위해서 5를 넣어줌

            rectList.add(rectF)
        }

//        이부분은 처음에 xml이 잘뜨는지 확인하기 위한 작업이었음.
//        rectF.top = 0f
//        rectF.bottom = maxAmplitude //아래값으로 늘어들고 줄어들고
//        rectF.left = 0f
//        rectF.right = rectF.left + 20f

        invalidate() // UI초기화 해서 onDraw함수를 다시 부름
    }

    fun replayAmplitude() {
        //재생시에는
        rectList.clear()

        val maxRect = (this.width / rectWidth).toInt()
        val amps = ampList.take(tick).takeLast(maxRect)

        for ((i, amp) in amps.withIndex()) {
            val rectF = RectF()
            rectF.top = (this.height / 2) - amp / 2 - 3f //위로절반 아래로 절반 가운데
            rectF.bottom = rectF.top + amp + 3f
            rectF.left = i * rectWidth
            rectF.right = rectF.left + rectWidth - 5f // 여백을 위해서 5를 넣어줌

            rectList.add(rectF)
        }
        tick++

        invalidate()// UI초기화 해서 onDraw함수를 다시 부름
    }

    fun clearData() {
        ampList.clear()
    }

    fun clearWave() {
        rectList.clear()
        tick = 0
        invalidate()
    }
}