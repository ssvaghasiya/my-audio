package com.example.myaudio

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat

class MusicService : Service(), MediaPlayer.OnCompletionListener {

    var mBinder: IBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    var musicFiles: ArrayList<MusicFiles> = ArrayList()
    var uri: Uri? = null
    var position: Int = -1
    var actionPlaying: ActionPlaying? = null
    var mediaSessionCompat: MediaSessionCompat? = null

    override fun onCreate() {
        super.onCreate()
        mediaSessionCompat = MediaSessionCompat(baseContext, "My Audio")
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.e("Bind", "Method")
        return mBinder
    }

    inner class MyBinder : Binder() {
        fun getService(): MusicService {
            return this@MusicService
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            var myPosition: Int? = intent!!.getIntExtra("servicePosition", -1)
            var actionName: String? = intent!!.getStringExtra("ActionName")

            if (myPosition != -1) {
                playMedia(myPosition!!)
            }

            if (actionName != null) {
                when (actionName) {
                    "playPause" -> {
                        //                    Toast.makeText(this, "PlayPause",Toast.LENGTH_LONG).show()
                        if (actionPlaying != null) {
                            actionPlaying!!.playPauseBtnClick()
                        }
                    }
                    "previous" -> {
                        //                    Toast.makeText(this, "previous",Toast.LENGTH_LONG).show()
                        if (actionPlaying != null) {
                            actionPlaying!!.prevBtnClick()
                        }
                    }
                    "next" -> {
                        //                    Toast.makeText(this, "next",Toast.LENGTH_LONG).show()
                        if (actionPlaying != null) {
                            actionPlaying!!.nextBtnClick()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return START_STICKY
    }

    private fun playMedia(startPosition: Int) {
        musicFiles = PlayerActivity.listSongs!!
        position = startPosition
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            if (musicFiles.isNotEmpty() || musicFiles != null) {
                createMediaPlayer(position)
                mediaPlayer?.start()
            }
        } else {
            createMediaPlayer(position)
            mediaPlayer?.start()
        }
    }

    fun start() {
        mediaPlayer?.start()
    }

    fun isPlaying(): Boolean? {
        return mediaPlayer?.isPlaying()
    }

    fun stop() {
        mediaPlayer?.stop()
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun release() {
        mediaPlayer?.release()
    }

    fun getDuration(): Int {
        return mediaPlayer?.duration!!
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    fun getCurrentPosition(): Int? {
        return mediaPlayer?.currentPosition
    }

    fun createMediaPlayer(positionInner: Int) {
        position = positionInner
        uri = Uri.parse(musicFiles.get(position).path)
        mediaPlayer = MediaPlayer.create(baseContext, uri)
    }

    fun OnCompleted() {
        mediaPlayer?.setOnCompletionListener(this)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        if (actionPlaying != null) {
            actionPlaying?.nextBtnClick()
            if (mediaPlayer != null) {
                createMediaPlayer(position)
                mediaPlayer?.start()
                OnCompleted()
            }
        }
    }


    fun setCallback(actionPlaying: ActionPlaying) {
        this.actionPlaying = actionPlaying
    }

    fun showNotification(playPauseBtn: Int) {
        var intent: Intent = Intent(this, PlayerActivity::class.java)
        var contentIntent = PendingIntent.getActivity(this, 0, intent, 0)

        var prevIntent: Intent = Intent(this, NotificationReceiver::class.java)
            .setAction(ApplicationClass.ACTION_PREVIOUS)
        var prevPending =
            PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        var pauseIntent: Intent = Intent(this, NotificationReceiver::class.java)
            .setAction(ApplicationClass.ACTION_PLAY)
        var pausePending =
            PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        var nextIntent: Intent = Intent(this, NotificationReceiver::class.java)
            .setAction(ApplicationClass.ACTION_NEXT)
        var nextPending =
            PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        var picture = getAlbumArt(musicFiles?.get(position)?.path!!)
        var thumb: Bitmap? = null
        if (picture != null) {
            thumb = BitmapFactory.decodeByteArray(picture, 0, picture.size)
        } else {
            thumb = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_foreground)
        }

        try {
            var notification = NotificationCompat.Builder(this, ApplicationClass.CHANNEL_ID_2!!)
                .setSmallIcon(playPauseBtn)
                .setLargeIcon(thumb)
                .setContentTitle(musicFiles?.get(position)?.title)
                .setContentText(musicFiles?.get(position)?.artist)
                .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", prevPending)
                .addAction(playPauseBtn, "Pause", pausePending)
                .addAction(R.drawable.ic_baseline_skip_next_24, "Next", nextPending)
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat?.sessionToken)
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build()

            startForeground(2, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAlbumArt(uri: String): ByteArray? {
        var art: ByteArray? = null
        try {
            var retriever: MediaMetadataRetriever = MediaMetadataRetriever()
            retriever.setDataSource(uri.toString())
            art = retriever.embeddedPicture
            retriever.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return art
    }

}