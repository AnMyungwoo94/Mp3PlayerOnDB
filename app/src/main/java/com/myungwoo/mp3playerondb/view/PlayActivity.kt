package com.myungwoo.mp3playerondb.view

import android.content.Intent
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
import com.myungwoo.mp3playerondb.service.MEDIA_PLAYER_PLAY
import com.myungwoo.mp3playerondb.service.MediaPlayerService
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

class PlayActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityPlayBinding
    private lateinit var musicData: MusicData
    private var playList: MutableList<Parcelable>? = null
    private var currentposition: Int = 0
    private val ALBUM_IMAGE_SIZE = 100
    private var mp3playerJob: Job? = null
    private var pauseFlag = false
    var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //1. 액션바대신에 툴바로 대체한다.
        setSupportActionBar(binding.toolbar)

        // 전달해온 intent 값을 가져옴
        playList = intent.getParcelableArrayListExtra("parcelableList")
        currentposition = intent.getIntExtra("position", 0)
        musicData = playList?.get(currentposition) as MusicData

        // 화면에 바인딩 진행 (activity_play와  MusicData 값을 이어줌)
        binding.albumTitle.text = musicData.title
        binding.albumArtist.text = musicData.artist
        binding.totalDuration.text = SimpleDateFormat("mm:ss").format(musicData.duration)
        binding.playDuration.text = "00:00"
        val bitmap = musicData.getAlbumBitmap(this, ALBUM_IMAGE_SIZE)
        if (bitmap != null) {
            binding.albumImage.setImageBitmap(bitmap)
        } else {
            binding.albumImage.setImageResource(R.drawable.music)
        }

        // 음악 파일객체 가져옴
        mediaPlayer = MediaPlayer.create(this, musicData.getMusicUri())

        // 이벤트 처리(일시정지, 실행, 돌아가기, 정지, 시크바 조절)
        binding.listButton.setOnClickListener(this)
        binding.playButton.setOnClickListener(this)
        binding.nextSongButton.setOnClickListener(this)
        binding.backSongButton.setOnClickListener(this)
        binding.shuffleButton.setOnClickListener(this)
        binding.seekBar.max = mediaPlayer!!.duration
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    // seekTo 해당하는 재생 위치로 이동
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.listButton -> {
//                val intent = Intent(this, MediaPlayerService::class.java)
//                    .apply { action = MEDIA_PLAYER_LIST }
//                startService(intent)

                //전체 목록으로 가는 버튼
                mp3playerJob?.cancel()
                mediaPlayer?.stop() //멈추기
                mediaPlayer?.release() // 할당된 메모리해제
                mediaPlayer = null
                finish() //끝내기
            }

            R.id.playButton -> {
                val intent = Intent(this, MediaPlayerService::class.java)
                    .apply { action = MEDIA_PLAYER_PLAY }
                intent.putExtra("musicData", musicData)
                startService(intent)

                if (mediaPlayer!!.isPlaying) {
//                    val intent = Intent(this, MediaPlayerService::class.java)
//                        .apply { action = MEDIA_PLAYER_PAUSE }
//                    startService(intent)

                    mediaPlayer?.pause() // 멈추기
                    binding.playButton.setImageResource(R.drawable.play_circle_24)
                    pauseFlag = true

                } else {
                    //노래 재생
                    mediaPlayer?.start()
                    binding.playButton.setImageResource(R.drawable.pause_circle_24)
                    pauseFlag = false

                    // 코루틴으로 음악을 재생
                    val backgroundScope = CoroutineScope(Dispatchers.Default + Job())
                    mp3playerJob = backgroundScope.launch {
                        while (mediaPlayer!!.isPlaying) {
                            var currentPosition = mediaPlayer?.currentPosition!!
                            // 코루틴속에서 화면의 값을 변동시키고자 할 때 runOnUiThread 사용
                            var strCurrentPosition =
                                SimpleDateFormat("mm:ss").format(mediaPlayer?.currentPosition)
                            runOnUiThread {
                                binding.seekBar.progress = currentPosition
                                binding.playDuration.text = strCurrentPosition
                            }
                            try {
                                delay(1000)
                                binding.seekBar.incrementProgressBy(1000)
                            } catch (e: java.lang.Exception) {
                                Log.e("PlayActivity", "delay 오류발생 ${e.printStackTrace()}")
                            }
                        }
                        if (!pauseFlag) {
                            runOnUiThread {
                                binding.seekBar.progress = 0
                                binding.playButton.setImageResource(R.drawable.play_circle_24)
                                binding.playDuration.text = "00:00"
                                //노래가 끝나면 다음곡으로 재생하게끔 진행(없으면 현재곡에서 멈춤)
                                if (currentposition < playList!!.size - 1) {
                                    ++currentposition
                                } else {
                                    currentposition = 0
                                }
                                setReplay()
                                start()
                            }
                        }
                    }
                }
            }

            R.id.nextSongButton -> {
                if (currentposition < playList!!.size - 1) {
                    ++currentposition
                } else {
                    currentposition = 0
                }
                setReplay()
                start()
            }

            R.id.backSongButton -> {
                if (currentposition == 0) {
                    currentposition = playList!!.size - 1
                } else {
                    --currentposition
                }
                setReplay()
                start()
            }

            R.id.shuffleButton -> {
                currentposition = (Math.random() * playList!!.size).toInt()
                setReplay()
                start()
            }
        }
    }


//    fun handleMediaAction(action: String) {
//        when (action) {
//            MEDIA_PLAYER_LIST -> {
//                val intent = Intent(this, MediaPlayerService::class.java)
//                intent.action = MEDIA_PLAYER_LIST
//                sendBroadcast(intent)
//
//                //전체 목록으로 가는 버튼
//                mp3playerJob?.cancel()
//                mediaPlayer?.stop() //멈추기
//                mediaPlayer?.release() // 할당된 메모리해제
//                mediaPlayer = null
//                finish() //끝내기
//            }
//
//            MEDIA_PLAYER_PLAY -> {
//                val intent = Intent(this, MediaPlayerService::class.java)
//                intent.action = MEDIA_PLAYER_PLAY
//                sendBroadcast(intent)
//
//                if (mediaPlayer!!.isPlaying) {
//                    val intent = Intent(this, MediaPlayerService::class.java)
//                    intent.action = MEDIA_PLAYER_PAUSE
//                    sendBroadcast(intent)
//
//                    mediaPlayer?.pause() // 멈추기
//                    binding.playButton.setImageResource(R.drawable.play_circle_24)
//                    pauseFlag = true
//
//                } else {
//                    //노래 재생
//                    mediaPlayer?.start()
//                    binding.playButton.setImageResource(R.drawable.pause_circle_24)
//                    pauseFlag = false
//
//                    // 코루틴으로 음악을 재생
//                    val backgroundScope = CoroutineScope(Dispatchers.Default + Job())
//                    mp3playerJob = backgroundScope.launch {
//                        while (mediaPlayer!!.isPlaying) {
//                            var currentPosition = mediaPlayer?.currentPosition!!
//                            // 코루틴속에서 화면의 값을 변동시키고자 할 때 runOnUiThread 사용
//                            var strCurrentPosition =
//                                SimpleDateFormat("mm:ss").format(mediaPlayer?.currentPosition)
//                            runOnUiThread {
//                                binding.seekBar.progress = currentPosition
//                                binding.playDuration.text = strCurrentPosition
//                            }
//                            try {
//                                delay(1000)
//                                binding.seekBar.incrementProgressBy(1000)
//                            } catch (e: java.lang.Exception) {
//                                Log.e("PlayActivity", "delay 오류발생 ${e.printStackTrace()}")
//                            }
//                        }
//                        if (!pauseFlag) {
//                            runOnUiThread {
//                                binding.seekBar.progress = 0
//                                binding.playButton.setImageResource(R.drawable.play_circle_24)
//                                binding.playDuration.text = "00:00"
//                                //노래가 끝나면 다음곡으로 재생하게끔 진행(없으면 현재곡에서 멈춤)
//                                if (currentposition < playList!!.size - 1) {
//                                    ++currentposition
//                                } else {
//                                    currentposition = 0
//                                }
//                                setReplay()
//                                start()
//                            }
//                        }
//                    }
//                }
//            }
//
//            MEDIA_PLAYER_NEXT -> {
//                val intent = Intent(this, MediaPlayerService::class.java)
//                intent.action = MEDIA_PLAYER_NEXT
//                sendBroadcast(intent)
//
//                if (currentposition < playList!!.size - 1) {
//                    ++currentposition
//                } else {
//                    currentposition = 0
//                }
//                setReplay()
//                start()
//            }
//
//            MEDIA_PLAYER_BACK -> {
//
//                val intent = Intent(this, MediaPlayerService::class.java)
//                intent.action = MEDIA_PLAYER_BACK
//                sendBroadcast(intent)
//
//                if (currentposition == 0) {
//                    currentposition = playList!!.size - 1
//                } else {
//                    --currentposition
//                }
//                setReplay()
//                start()
//            }
//
//            MEDIA_PLAYER_SHUFFLE -> {
//
//                val intent = Intent(this, MediaPlayerService::class.java)
//                intent.action = MEDIA_PLAYER_SHUFFLE
//                sendBroadcast(intent)
//
//                currentposition = (Math.random() * playList!!.size).toInt()
//                setReplay()
//                start()
//            }
//
//            MEDIA_PLAYER_STOP -> {
//                val intent = Intent(this, MediaPlayerService::class.java)
//                intent.action = MEDIA_PLAYER_STOP
//                Log.e("스탑", "스탑")
//                sendBroadcast(intent)
//
//                mp3playerJob?.cancel()
//                mediaPlayer?.stop() //멈추기
//                mediaPlayer?.release() // 할당된 메모리해제
//                mediaPlayer = null
//                finish()
//            }
//        }
//    }

    private fun setReplay() {
        mediaPlayer?.stop()
        mp3playerJob?.cancel()
        musicData = playList?.get(currentposition) as MusicData

        // 음악 파일객체 가져옴
        mediaPlayer = MediaPlayer.create(this, musicData?.getMusicUri())
        binding.seekBar.progress = 0
        binding.playDuration.text = "00:00"

        // 화면에 바인딩 진행 (activity_play와  MusicData 값을 이어줌)
        binding.albumTitle.text = musicData?.title
        binding.albumArtist.text = musicData?.artist
        binding.totalDuration.text = SimpleDateFormat("mm:ss").format(musicData?.duration)
        binding.playDuration.text = "00:00"
        val bitmap = musicData?.getAlbumBitmap(this, ALBUM_IMAGE_SIZE)
        if (bitmap != null) {
            binding.albumImage.setImageBitmap(bitmap)
        } else {
            binding.albumImage.setImageResource(R.drawable.music)
        }
    }

    private fun start() {
        //노래 재생
        mediaPlayer?.start()
        binding.playButton.setImageResource(R.drawable.pause_circle_24)
        pauseFlag = false

        // 코루틴으로 음악을 재생
        val backgroundScope = CoroutineScope(Dispatchers.Default + Job())
        mp3playerJob = backgroundScope.launch {
            while (mediaPlayer!!.isPlaying) {
                var currentPosition = mediaPlayer?.currentPosition!!
                // 코루틴속에서 화면의 값을 변동시키고자 할 때 runOnUiThread 사용
                var strCurrentPosition =
                    SimpleDateFormat("mm:ss").format(mediaPlayer?.currentPosition)
                runOnUiThread {
                    binding.seekBar.progress = currentPosition
                    binding.playDuration.text = strCurrentPosition
                }
                try {
                    delay(1000)
                    binding.seekBar.incrementProgressBy(1000)
                } catch (e: java.lang.Exception) {
                    Log.e("PlayActivity", "delay 오류발생 ${e.printStackTrace()}")
                }
            }
            if (!pauseFlag) {
                runOnUiThread {
                    binding.seekBar.progress = 0
                    binding.playButton.setImageResource(R.drawable.play_circle_24)
                    binding.playDuration.text = "00:00"
                }
            }
        }
    }

    override fun onBackPressed() {
        mp3playerJob?.cancel()
        mediaPlayer?.stop()
        finish()
    }

}