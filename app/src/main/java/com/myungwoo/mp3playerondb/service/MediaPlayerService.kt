package com.myungwoo.mp3playerondb.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.drawable.Icon
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import com.myungwoo.mp3playerondb.data.MusicData
import com.myungwoo.mp3playerondb.ui.PlayActivity
import com.myungwoo.mp3playerondb.R

class MediaPlayerService : Service() {
    private var mediaPlayer: MediaPlayer? = null

    companion object {
        const val CHANNEL_ID = "MEDIA_PLAYER_CHANNEL"
        const val MEDIA_PLAYER_PLAY = "MEDIA_PLAYER_PLAY"
        const val MEDIA_PLAYER_PAUSE = "MEDIA_PLAYER_PAUSE"
        const val MEDIA_PLAYER_STOP = "MEDIA_PLAYER_STOP"
    }


    // 내부 클래스로 MediaPlayerBinder 추가
    inner class MediaPlayerBinder : Binder() {
        fun getService(): MediaPlayerService = this@MediaPlayerService
    }

    // binder 변수 초기화를 늦게 할당하도록 수정
    private var binder: IBinder? = null
    var isServiceBound = false

    private lateinit var playIcon: Icon
    lateinit var playPendingIntent: PendingIntent
    private lateinit var pauseIcon: Icon
    lateinit var pausePendingIntent: PendingIntent
    private lateinit var stopIcon: Icon
    private lateinit var stopPendingIntent: PendingIntent
    private lateinit var mainPendingIntent: PendingIntent

    override fun onBind(intent: Intent): IBinder? {
        return binder
        //바인드 서비스가 아닌 포그라운드 서비스를 구현할거라 사용하지 않을 예정
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

       playIcon = Icon.createWithResource(baseContext, R.drawable.ic_play_circle_24)
        playPendingIntent = PendingIntent.getService(
            baseContext,
            0,
            Intent(baseContext, MediaPlayerService::class.java).apply {
                action = MEDIA_PLAYER_PLAY
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        pauseIcon = Icon.createWithResource(baseContext, R.drawable.ic_pause_24)
        pausePendingIntent = PendingIntent.getService(
            baseContext,
            0,
            Intent(baseContext, MediaPlayerService::class.java).apply {
                action = MEDIA_PLAYER_PAUSE
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        stopIcon = Icon.createWithResource(baseContext, R.drawable.ic_record_stop_24)
      stopPendingIntent = PendingIntent.getService(
            baseContext,
            0,
            Intent(baseContext, MediaPlayerService::class.java).apply {
                action = MEDIA_PLAYER_STOP
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        mainPendingIntent = PendingIntent.getActivity(
            baseContext,
            0,
            Intent(baseContext, PlayActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = Notification.Builder(baseContext, CHANNEL_ID)
            .setStyle(Notification.MediaStyle().setShowActionsInCompactView(0, 1, 2))
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.ic_play_asmr_24)
            .addAction(Notification.Action.Builder(playIcon, "재생", playPendingIntent).build())
            .addAction(Notification.Action.Builder(pauseIcon, "멈춤", pausePendingIntent).build())
            .addAction(Notification.Action.Builder(stopIcon, "정지", stopPendingIntent).build())
            .setContentIntent(mainPendingIntent)
            .setContentTitle("음악 재생")
            .setContentText("음원이 재생 중입니다.")

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.build()
        } else {
            notificationBuilder.notification
        }
        startForeground(100, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MEDIA_PLAYER"
            val description = "음악 재생 중에 표시되는 알림"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                this.description = description
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    //서비스가 실행이 되고 oncreate가 된 다음에 불러오는 것
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        binder = MediaPlayerBinder()
        when (intent?.action) {
            MEDIA_PLAYER_PLAY -> {
                val musicData = intent.getParcelableExtra<MusicData>("musicData")
                if (musicData != null) {
                    togglePlayback(musicData)
                }
            }
            MEDIA_PLAYER_PAUSE -> {
                pausePlayback()
            }
            MEDIA_PLAYER_STOP -> {
                stopPlayback()
            }
        }

        // 수정된 부분: 노티피케이션 업데이트 함수 호출
        updateNotification(
            NotificationCompat.Action.Builder(
                R.drawable.ic_pause_24,
                "멈춤",
                pausePendingIntent
            ).build()
        )
        return super.onStartCommand(intent, flags, startId)
    }

    private fun togglePlayback(musicData: MusicData) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(baseContext, musicData.getMusicUri())
        }

        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            updateNotification(
                NotificationCompat.Action.Builder(
                    R.drawable.ic_play_circle_24,
                    "재생",
                    playPendingIntent
                ).build()
            )
        } else {
            mediaPlayer?.start()
            updateNotification(
                NotificationCompat.Action.Builder(
                    R.drawable.ic_pause_24,
                    "멈춤",
                    pausePendingIntent
                ).build()
            )
        }
    }

    private fun pausePlayback() {
        mediaPlayer?.pause()
        updateNotification(
            NotificationCompat.Action.Builder(
                R.drawable.ic_play_circle_24,
                "재생",
                playPendingIntent
            ).build()
        )
    }

    private fun stopPlayback() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        stopSelf()
    }

     fun updateNotification(action: NotificationCompat.Action) {
        val notificationBuilder = NotificationCompat.Builder(baseContext, CHANNEL_ID)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.ic_play_asmr_24)
            .addAction(action)
            .addAction(
                NotificationCompat.Action.Builder(
                    IconCompat.createWithResource(baseContext, R.drawable.ic_pause_24),
                    "멈춤",
                    pausePendingIntent
                ).build()
            )
            .addAction(
                NotificationCompat.Action.Builder(
                    IconCompat.createWithResource(baseContext, R.drawable.ic_record_stop_24),
                    "정지",
                    stopPendingIntent
                ).build()
            )
            .setContentIntent(mainPendingIntent)
            .setContentTitle("음악 재생")
            .setContentText("음원이 재생 중입니다.")

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.build()
        } else {
            notificationBuilder.notification
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(100, notification)


    }


}