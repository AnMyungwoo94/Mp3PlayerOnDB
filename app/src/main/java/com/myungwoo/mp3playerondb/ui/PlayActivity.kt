package com.myungwoo.mp3playerondb.ui

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.SeekBar
import com.myungwoo.mp3playerondb.data.MusicData
import com.myungwoo.mp3playerondb.R
import com.myungwoo.mp3playerondb.databinding.ActivityPlayBinding
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

class PlayActivity : AppCompatActivity(), View.OnClickListener {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var binding: ActivityPlayBinding
    private lateinit var musicData: MusicData
    private var playList: MutableList<Parcelable>? = null
    private var currentposition: Int = 0
    private val ALBUM_IMAGE_SIZE = 100
    private var mp3playerJob: Job? = null
    private var pauseFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //액션바 툴바로 변경
        setSupportActionBar(binding.toolbarPlay)

        playList = intent.getParcelableArrayListExtra("parcelableList")
        currentposition = intent.getIntExtra("position", 0)
        musicData = playList?.get(currentposition) as MusicData
        binding.sbPlay.max = Int.MAX_VALUE
        binding.tvPlayAlbumTitle.text = musicData.title
        binding.tvPlayAlbumArtist.text = musicData.artist
        binding.tvPlayTotalDuration.text = SimpleDateFormat("mm:ss").format(musicData.duration)
        binding.tvPlayDuration.text = "00:00"
        val bitmap = musicData.getAlbumBitmap(this, ALBUM_IMAGE_SIZE)
        if (bitmap != null) {
            binding.cvPlayAlbumImage.setImageBitmap(bitmap)
        } else {
            binding.cvPlayAlbumImage.setImageResource(R.drawable.iv_music)
        }
        //음악 데이터 가져오기
        mediaPlayer = MediaPlayer.create(this, musicData.getMusicUri())

        binding.ibPlayAllList.setOnClickListener(this)
        binding.ibPlayPlay.setOnClickListener(this)
        binding.ibPlayNextSong.setOnClickListener(this)
        binding.ibPlayBackSong.setOnClickListener(this)
        binding.ibPlayShuffle.setOnClickListener(this)
        binding.sbPlay.max = mediaPlayer!!.duration
        binding.sbPlay.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ib_play_all_list -> {
                mp3playerJob?.cancel()
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
                finish()
            }

            R.id.ib_play_play -> {
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                    binding.ibPlayPlay.setImageResource(R.drawable.ic_play_circle_24)
                    pauseFlag = true
                } else {
                    mediaPlayer?.start()
                    binding.ibPlayPlay.setImageResource(R.drawable.ic_pause_24)
                    pauseFlag = false
                    playMusic()

                    // 추가: 다음 곡으로 자동 이동
                    mediaPlayer?.setOnCompletionListener {
                        if (!pauseFlag) {
                            // 다음 곡으로 이동
                            nextSong()
                        }
                    }
                }
            }

            R.id.ib_play_next_song -> {
                nextSong()
            }

            R.id.ib_play_back_song -> {
                if (currentposition == 0) {
                    currentposition = playList!!.size - 1
                } else {
                    --currentposition
                }
                setReplay()
                mediaPlayer?.start()
                playMusic()
            }

            R.id.ib_play_shuffle -> {
                currentposition = (Math.random() * playList!!.size).toInt()
                setReplay()
                mediaPlayer?.start()
                playMusic()
            }
        }
    }

    private fun setReplay() {
        mediaPlayer?.stop()
        mp3playerJob?.cancel()
        musicData = playList?.get(currentposition) as MusicData
        mediaPlayer = MediaPlayer.create(this, musicData?.getMusicUri())
        binding.sbPlay.progress = 0
        binding.tvPlayDuration.text = "00:00"
        binding.tvPlayAlbumTitle.text = musicData?.title
        binding.tvPlayAlbumArtist.text = musicData?.artist
        binding.tvPlayTotalDuration.text = SimpleDateFormat("mm:ss").format(musicData?.duration)
        binding.tvPlayDuration.text = "00:00"
        val bitmap = musicData?.getAlbumBitmap(this, ALBUM_IMAGE_SIZE)
        if (bitmap != null) {
            binding.cvPlayAlbumImage.setImageBitmap(bitmap)
        } else {
            binding.cvPlayAlbumImage.setImageResource(R.drawable.iv_music)
        }
    }

    private fun playMusic() {
        val backgroundScope = CoroutineScope(Dispatchers.Default + Job())
        mp3playerJob = backgroundScope.launch {
            while (mediaPlayer!!.isPlaying) {
                var currentPosition = mediaPlayer?.currentPosition!!
                var strCurrentPosition =
                    SimpleDateFormat("mm:ss").format(mediaPlayer?.currentPosition)
                runOnUiThread {
                    binding.sbPlay.progress = currentPosition
                    binding.tvPlayDuration.text = strCurrentPosition

                    // mediaPlayer의 재생 상태에 따라 재생 버튼 이미지 변경
                    if (mediaPlayer!!.isPlaying) {
                        binding.ibPlayPlay.setImageResource(R.drawable.ic_pause_24)
                    } else {
                        binding.ibPlayPlay.setImageResource(R.drawable.ic_play_circle_24)
                    }

                    // 뷰를 갱신
                    binding.root.invalidate()
                }
                try {
                    delay(1000)
                } catch (e: java.lang.Exception) {
                    Log.e("PlayActivity", "delay 오류발생 ${e.printStackTrace()}")
                }
            }
            if (!pauseFlag) {
                runOnUiThread {
                    binding.sbPlay.progress = 0
                    binding.ibPlayPlay.setImageResource(R.drawable.ic_play_circle_24)
                    binding.tvPlayDuration.text = "00:00"
                    // 뷰를 갱신
                    binding.root.invalidate()
                }
            }
        }
    }

    private fun nextSong() {
        if (currentposition < playList!!.size - 1) {
            ++currentposition
        } else {
            currentposition = 0
        }
        setReplay()
        mediaPlayer?.start()
        playMusic()
    }

    override fun onBackPressed() {
        mp3playerJob?.cancel()
        mediaPlayer?.stop()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        mp3playerJob?.cancel()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        finish()
        Log.e("라이프사이클", "onDestroy()")
    }
}