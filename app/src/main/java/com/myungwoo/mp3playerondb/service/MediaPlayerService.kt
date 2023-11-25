package com.myungwoo.mp3playerondb.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.drawable.Icon
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.myungwoo.mp3playerondb.data.MusicData
import com.myungwoo.mp3playerondb.view.PlayActivity
import com.myungwoo.mp3playerondb.R


class MediaPlayerService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
        //바인드 서비스가 아닌 포그라운드 서비스를 구현할거라 사용하지 않을 예정
    }
    companion object {
        const val ACTION_TOGGLE_PLAYBACK = "com.myungwoo.mp3playerondb.action.TOGGLE_PLAYBACK"
        const val ACTION_STOP_PLAYBACK = "com.myungwoo.mp3playerondb.action.STOP_PLAYBACK"
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        val playIcon = Icon.createWithResource(baseContext, R.drawable.play_circle_24)
        val playPendingIntent = PendingIntent.getService(
            baseContext,
            0,
            Intent(baseContext, MediaPlayerService::class.java).apply {
                action = MEDIA_PLAYER_PLAY
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val pauseIcon = Icon.createWithResource(baseContext, R.drawable.pause_circle_24)
        val pausePendingIntent = PendingIntent.getService(
            baseContext,
            0,
            Intent(baseContext, MediaPlayerService::class.java).apply {
                action = MEDIA_PLAYER_PAUSE
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopIcon = Icon.createWithResource(baseContext, R.drawable.stop_circle_24)
        val stopPendingIntent = PendingIntent.getService(
            baseContext,
            0,
            Intent(baseContext, MediaPlayerService::class.java).apply {
                action = MEDIA_PLAYER_STOP
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val mainPendingIntent = PendingIntent.getActivity(
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
            .setSmallIcon(R.drawable.play_asmr_24)
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
        when (intent?.action) {
            ACTION_TOGGLE_PLAYBACK -> {
                val musicData = intent.getParcelableExtra<MusicData>("musicData")
                if (musicData != null) {
                    togglePlayback(musicData)
                }
            }
            ACTION_STOP_PLAYBACK -> {
                stopPlayback()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun togglePlayback(musicData: MusicData) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(baseContext, musicData.getMusicUri())
        }

        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        } else {
            mediaPlayer?.start()
        }
    }

    private fun stopPlayback() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        stopSelf()
    }


}