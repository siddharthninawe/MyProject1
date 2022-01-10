package com.example.myproject1.ui.services

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.example.myproject1.MainActivity
import com.example.myproject1.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

private const val MSG_SAY_PLAY = 111
private const val MSG_SAY_Pause = 112
private const val MSG_SAY_STOP = 113

class MusicDownloadService : Service() {
    private var id = "channelID"
    private var name = "channelName"

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var messenger: Messenger
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var notification: Notification

    override fun onCreate() {
        Log.d(MusicDownloadService::class.java.name, "onCreate")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(MusicDownloadService::class.java.name, "onStartCommand")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT)
            // val channel = NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT).build()
            notificationManager = NotificationManagerCompat.from(this)
            notificationManager.createNotificationChannel(channel)
        }

        val notify = Intent(this, MainActivity::class.java)
        Log.d(MusicDownloadService::class.java.name, "notify : $notify")
        Log.d(MusicDownloadService::class.java.name, "intent : $intent")
        val pendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(notify)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        notification = NotificationCompat.Builder(this, id)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Example Title")
            .setContentText("Example Content")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        startForeground(1, notification)
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(MusicDownloadService::class.java.name, "onDestroy")
        stopForeground(true)
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d(MusicDownloadService::class.java.name, "onBind")
        messenger = Messenger(IncomingHandler(this))
        return messenger.binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(MusicDownloadService::class.java.name, "UnBind")
        return true
    }


    @SuppressLint("HandlerLeak")
    inner class IncomingHandler(
        context: Context,
        private val applicationContext: Context = context.applicationContext
    ) : Handler(Looper.myLooper()!!) {
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

