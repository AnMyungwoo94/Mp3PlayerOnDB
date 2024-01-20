package com.myungwoo.mp3playerondb.ui

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
import com.myungwoo.mp3playerondb.recordwave.OnTimerTickListener
import com.myungwoo.mp3playerondb.recordwave.Timer
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
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var fileName: String = ""
    private var state: State = State.RELEASE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fileName = "${externalCacheDir?.absolutePath}/audiorecoder.3gp"
        timer = Timer(this)
        binding.recordButton.setOnClickListener {
            when (state) {
                State.RELEASE -> {
                    checkRecordePermissionStart()
                }

                State.RECORDING -> {
                    onRecord(false)
                }

                State.PLAYING -> {
                    //나중에 필요할 때 작성하기!
                }
            }
        }
        binding.recoadPlayButton.setOnClickListener {
            when (state) {
                State.RELEASE -> {
                    onplay(true)
                }

                else -> {
                    // do nothing
                }
            }
        }
        binding.recoadPlayButton.isEnabled = false
        binding.recoadPlayButton.alpha = 0.3f

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

    private fun checkRecordePermissionStart() {
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
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            try {
                prepare()
            } catch (e: IOException) {
                Log.e("RECODER", e.toString())
            }
            start()
        }

        binding.waveformView.clearData()
        timer.start()
        binding.recordButton.setImageDrawable(
            ContextCompat.getDrawable(
                this, R.drawable.ic_record_stop_24
            )
        )
        binding.recordButton.imageTintList = ColorStateList.valueOf(Color.BLACK)
        binding.recoadPlayButton.isEnabled = false
        binding.recoadPlayButton.alpha = 0.3f
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
                this, R.drawable.ic_record_24
            )
        )
        binding.recordButton.imageTintList = ColorStateList.valueOf(Color.RED)
        binding.recoadPlayButton.isEnabled = true
        binding.recoadPlayButton.alpha = 1.0f
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
            .setMessage(R.string.record_permission_explanation)
            .setPositiveButton(R.string.record_ok) { _, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_RECORD_AUDIO_CODE
                )
            }.setNegativeButton(R.string.record_cancel) { dialogInterface, _ -> dialogInterface.cancel() }
            .show()
    }

    private fun showPermissionSettingDialog() {
        AlertDialog.Builder(this)
            .setMessage(R.string.record_permission_message)
            .setPositiveButton(R.string.record_setting) { _, _ ->
                navigateToAppSetting()
            }.setNegativeButton(R.string.record_cancel) { dialogInterface, _ -> dialogInterface.cancel() }
            .show()
    }

    private fun navigateToAppSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val audioRecordPermissionGranted = requestCode == REQUEST_RECORD_AUDIO_CODE
                && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED

        if (audioRecordPermissionGranted) {
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
            binding.waveformView.replayAmplitude()
        } else if (state == State.RECORDING) {
            binding.waveformView.addAmplitude(recorder?.maxAmplitude?.toFloat() ?: 0f)
        }
    }
}