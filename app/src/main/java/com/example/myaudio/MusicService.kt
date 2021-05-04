package com.example.myaudio

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log

class MusicService : Service(), MediaPlayer.OnCompletionListener {

    var mBinder: IBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    var musicFiles: ArrayList<MusicFiles> = ArrayList()
    var uri: Uri? = null
    var position: Int = -1
    var actionPlaying: ActionPlaying? = null

    override fun onCreate() {
        super.onCreate()
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
        var myPosition: Int = intent!!.getIntExtra("servicePosition", -1)
        if (myPosition != -1) {
            playMedia(myPosition)
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

    fun createMediaPlayer(position: Int) {
        uri = Uri.parse(musicFiles.get(position).path)
        mediaPlayer = MediaPlayer.create(baseContext, uri)
    }

    fun OnCompleted() {
        mediaPlayer?.setOnCompletionListener(this)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        if (actionPlaying != null) {
            actionPlaying?.nextBtnClick()
        }
        createMediaPlayer(position)
        mediaPlayer?.start()
        OnCompleted()
    }

}