package com.myungwoo.mp3playerondb.record

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.myungwoo.mp3playerondb.databinding.ActivityRecordBinding
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.myungwoo.mp3playerondb.R
import java.io.IOException

class RecordActivity : AppCompatActivity(), OnTimerTickListener {
    companion object {
        private const val REQUEST_RECORD_AUDIO_CODE = 200
    }

    private enum class State {
        RELEASE, RECORDING, PLAYING
    }

    private lateinit var binding: ActivityRecordBinding
    private lateinit var timer: Timer
    private var recorder: MediaRecorder? = null //레코더 실행
    private var player: MediaPlayer? = null //플레이 실행
    private var fileName: String = "" //파일로 저장하기
    private var state: State = State.RELEASE


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fileName = "${externalCacheDir?.absolutePath}/audiorecoder.3gp" //파일 저장하기
        timer = Timer(this)

        //1. 권한요청을 하면
        //2.  onRequestPermissionsResult에서 응답을 받아
        binding.recordButton.setOnClickListener {
            when (state) {
                State.RELEASE -> {
                    ChackRecordePermissionStart()
                }

                State.RECORDING -> {
                    onRecord(false)
                }

                State.PLAYING -> {

                }
            }
        }

        binding.playButton.setOnClickListener {
            when (state) {
                State.RELEASE -> {
                    onplay(true)
                }

                else -> {
                    // do nothing
                }
            }
        }
        //처음에는 플레이버튼 실행 안되도록
        binding.playButton.isEnabled = false
        binding.playButton.alpha = 0.3f

        binding.stopButton.setOnClickListener {
            when (state) {
                State.PLAYING -> {
                    onplay(false)
                }

                else -> {
                    // do nothing
                }
            }
        }
    }

    private fun ChackRecordePermissionStart() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                onRecord(true)
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.RECORD_AUDIO
            ) -> {
                showPermissionRationalDialog()
            }

            else -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_RECORD_AUDIO_CODE
                )
            }
        }
    }

    private fun onRecord(start: Boolean) = if (start) startRecoding() else stopRecoding()

    private fun onplay(start: Boolean) = if (start) startPlaying() else stopPlaying()

    private fun startRecoding() {
        state = State.RECORDING

        //오디오 레코드 시작
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC) //마이크를 사용하겠다.
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP) //THREE_GPP로 저장하겠다.
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB) //인코더 사용
            try {
                prepare() //준비
            } catch (e: IOException) {
                Log.e("RECODER", e.toString())
            }
            start()
        }

        binding.waveformView.clearData()
        timer.start()

        //진폭 받아오기/ maxAmplitude 최대 진폭 말안하면 작게 말하면 크게
        //스타트레코딩은 한번 누르면 한번만 반환되는 함수.
        //진폭을 실기간으로 가져와야해서 콜백을 사용해야함, 주기적으로 체크할 수 있는 쓰레드 만들어야해 그게 Timer클래스
        //recorder?.maxAmplitude?.toFloat() 원래 이거 사용했다가 아래로 변경 흐름상
        binding.recordButton.setImageDrawable(
            ContextCompat.getDrawable(
                this, R.drawable.ic_baseline_stop_24
            )
        )
        //오디오 실행시 버튼 조절하기
        binding.recordButton.imageTintList = ColorStateList.valueOf(Color.BLACK)
        binding.playButton.isEnabled = false //재생버튼은 더이상 눌리지 않음
        binding.playButton.alpha = 0.3f //재생버튼이 조금 흐려짐(안됨 표시)
    }

    private fun stopRecoding() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        timer.stop()
        state = State.RELEASE

        binding.recordButton.setImageDrawable(
            ContextCompat.getDrawable(
                this, R.drawable.ic_baseline_fiber_manual_record_24
            )
        )
        binding.recordButton.imageTintList = ColorStateList.valueOf(Color.RED)
        binding.playButton.isEnabled = true //재생버튼은 더이상 눌리지 않음
        binding.playButton.alpha = 1.0f //재생버튼이 조금 흐려짐(안됨 표시)
    }

    private fun startPlaying() {
        state = State.PLAYING

        player = MediaPlayer().apply {
            setDataSource(fileName)
            try {
                prepare() //준비
            } catch (e: IOException) {
                Log.e("플레이 오류", e.toString())
            }
            start()
        }

        //etOnCompletionListener을 사용하여 끝났을때를 알고 stop으로 바꿔줌
        player?.setOnCompletionListener {
            stopPlaying()
        }
        binding.waveformView.clearWave()
        timer.start()

        binding.recordButton.isEnabled = false
        binding.recordButton.alpha = 0.3f
    }

    private fun stopPlaying() {
        state = State.RELEASE

        player?.release()
        player = null

        timer.stop()

        binding.recordButton.isEnabled = true
        binding.recordButton.alpha = 1.0f
    }

    private fun showPermissionRationalDialog() {
        AlertDialog.Builder(this)
            .setMessage("녹음 권한을 켜주셔야지 앱을 정상적으로 사용할 수 있습니다.")
            .setPositiveButton("권한 허용하기") { _, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_RECORD_AUDIO_CODE
                )
            }.setNegativeButton("취소") { dialogInterface, _ -> dialogInterface.cancel() }
            .show()
    }

    private fun showPermissionSettingDialog() {
        AlertDialog.Builder(this)
            .setMessage(R.string.permission_message)
            .setPositiveButton("권한 변경하러 가기") { _, _ ->
                navigateToAppSetting()
            }.setNegativeButton("취소") { dialogInterface, _ -> dialogInterface.cancel() }
            .show()
    }

    private fun navigateToAppSetting() {
        //휴대폰 설정으로 이동하기
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null) //우리앱의 디테일 세팅으로 가기
        }
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //요청한 퍼미션을 응답받음

        val audioRecordPermissionGranted = requestCode == REQUEST_RECORD_AUDIO_CODE
                && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED

        if (audioRecordPermissionGranted) {
            //녹음작업 시작
            onRecord(true)
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.RECORD_AUDIO
                )
            ) {
                showPermissionRationalDialog()
            } else {
                showPermissionSettingDialog()
            }
        }
    }

    override fun onTick(duration: Long) {
        val millisecond = duration % 1000
        val second = (duration / 1000) % 60
        val minute = (duration / 1000 / 60)

        binding.timerTextView.text =
            String.format("%02d:%02d.%02d", minute, second, millisecond / 10)

        if (state == State.PLAYING) {
            binding.waveformView.replayAmplitude() //uration.toInt() 사용하지는 않아
        } else if (state == State.RECORDING) {
            binding.waveformView.addAmplitude(recorder?.maxAmplitude?.toFloat() ?: 0f)
        }
    }
}