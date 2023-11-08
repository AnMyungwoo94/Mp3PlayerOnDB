package com.myungwoo.mp3playerondb.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.drawable.Icon
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import com.myungwoo.mp3playerondb.PlayActivity
import com.myungwoo.mp3playerondb.R

class MediaPlayerService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
        //바인드 서비스가 아닌 포그라운드 서비스를 구현할거라 사용하지 않을 예정
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        //Notification에 띄워줄 아이콘
        val plauIcon = Icon.createWithResource(baseContext, R.drawable.play_circle_24)

        //Notification에 실행 될 내용
        val playPeandingIntent = PendingIntent.getService(
            baseContext,
            0,
            Intent(baseContext, MediaPlayerService::class.java).apply {
                action = MEDIA_PLAYER_PLAY
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        //Notification에 자체를 눌렀을 때 재생화면으로 가게 설정
        val mainPEndingIntent = PendingIntent.getActivity(
            baseContext,
            0,
            Intent(baseContext, PlayActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP //있을 경우 새로운걸 만들지 않고, 가져다가 사용함
                },
            PendingIntent.FLAG_IMMUTABLE
        )

        //Notification 만들기
        val notification = Notification.Builder(baseContext, CHANNEL_ID)
            .setStyle(
                Notification.MediaStyle()
                    .setShowActionsInCompactView(0) // 버튼 몇개를 쓸건지
            )
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.play_asmr_24)
            .setActions(
                Notification.Action.Builder(
                    plauIcon,
                    "play",
                    playPeandingIntent
                ).build()
            )
            .setContentIntent(mainPEndingIntent) //notification 자체를 눌렀을때 나옴
            .setContentTitle("음악재생")
            .setContentText("음원이 재생 중 입니다.") //바디에 나올 내용
            .build()

        startForeground(100, notification)
    }

    private fun createNotificationChannel() {
        val channel =
            NotificationChannel(CHANNEL_ID, "MEDIA_PLAYER", NotificationManager.IMPORTANCE_DEFAULT)

        val notificationManager = baseContext.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    //서비스가 실행이 되고 oncreate가 된 다음에 불러오는 것
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            MEDIA_PLAYER_PLAY -> {
                Log.e("서비스 진행중", "서비스 진행중")
                mediaPlayer?.start()

            }

            MEDIA_PLAYER_PAUSE -> {

            }

            MEDIA_PLAYER_STOP -> {

            }

        }
        return super.onStartCommand(intent, flags, startId)
    }
}