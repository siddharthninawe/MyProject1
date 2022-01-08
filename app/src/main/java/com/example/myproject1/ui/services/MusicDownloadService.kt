package com.example.myproject1.ui.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myproject1.R
import com.example.myproject1.ui.home.HomeFragment

private const val MSG_SAY_PLAY = 111
private const val MSG_SAY_Pause = 112
private const val MSG_SAY_STOP = 113

class MusicDownloadService : Service() {
    private val CHANNEL_ID: String = "com.example.myproject1.channelExample1"
    private val NOTIFICATION_ID = 101

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var messenger: Messenger

    var liveData: LiveData<Int> = MutableLiveData()

    override fun onCreate() {
        Log.d(MusicDownloadService::class.java.name, "onCreate")
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Title"
            val descText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description =descText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notify = Intent(this, HomeFragment::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivities(this, 0, arrayOf(notify), 0)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Example Title")
            .setContentText("Example Content")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            builder.build().let {
                startForeground(1, it)
                notify(NOTIFICATION_ID, it)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(MusicDownloadService::class.java.name, "onStartCommand")



        return START_NOT_STICKY
    }

    override fun onStart(intent: Intent?, startId: Int) {
        Log.d(MusicDownloadService::class.java.name, "onStart")
        super.onStart(intent, startId)
    }

    override fun onDestroy() {
        Log.d(MusicDownloadService::class.java.name, "onDestroy")
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d(MusicDownloadService::class.java.name, "onBind")
        messenger = Messenger(IncomingHandler(this))
        return messenger.binder
    }

    inner class IncomingHandler(context: Context, private val applicationContext: Context = context.applicationContext) : Handler() {
        override fun handleMessage(msg: Message) {
            when(msg.what) {
                MSG_SAY_PLAY -> {
                    if(mediaPlayer == null) {
                        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.main_agar)
                    }
                    mediaPlayer?.start()
                    Log.d(MusicDownloadService::class.java.name,
                        mediaPlayer!!.currentPosition.toString()
                    )
                }

                MSG_SAY_Pause -> {
                    if(mediaPlayer != null) {
                        mediaPlayer!!.pause()
                        Log.d(MusicDownloadService::class.java.name,
                            mediaPlayer!!.currentPosition.toString()
                        )
                    }
                }

                MSG_SAY_STOP -> {
                    if(mediaPlayer != null) {
                        mediaPlayer!!.release()
                        mediaPlayer = null
                        Toast.makeText(applicationContext, "MediaPlayer Released", Toast.LENGTH_LONG).show()
                    }
                }
            }
            super.handleMessage(msg)
        }
    }
}