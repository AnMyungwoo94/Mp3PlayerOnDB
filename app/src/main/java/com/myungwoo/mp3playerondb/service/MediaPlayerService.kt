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
import com.myungwoo.mp3playerondb.MusicData
import com.myungwoo.mp3playerondb.PlayActivity
import com.myungwoo.mp3playerondb.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.start
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat


class MediaPlayerService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
        //바인드 서비스가 아닌 포그라운드 서비스를 구현할거라 사용하지 않을 예정
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
        val shardMusicData = intent?.getParcelableExtra<MusicData>("musicData")
        Log.e("서비스에 있는 뮤직데이터", shardMusicData.toString())

        when (intent?.action) {
            MEDIA_PLAYER_PLAY -> {
                if (mediaPlayer == null) {
                    // 음악 파일객체 가져옴
                    mediaPlayer = MediaPlayer.create(baseContext, shardMusicData?.getMusicUri())
                    Log.e("서비스에 있는 뮤직데이터2", mediaPlayer.toString())
                }
                    mediaPlayer?.start()

            }

            MEDIA_PLAYER_PAUSE -> {
                Log.e("서비스 진행중", "일시 멈춤")
                mediaPlayer?.pause()
            }

            MEDIA_PLAYER_STOP -> {
                Log.e("서비스 진행중", "정지")
                mediaPlayer?.stop() //멈추기
                mediaPlayer?.release() // 할당된 메모리해제
                mediaPlayer = null
                stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }


}