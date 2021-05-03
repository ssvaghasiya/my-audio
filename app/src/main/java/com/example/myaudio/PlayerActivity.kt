package com.example.myaudio

import android.app.PendingIntent.getActivity
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_player.*

class PlayerActivity : AppCompatActivity() {

    var position: Int = 0
    var handler: Handler = Handler()
    var playThread: Thread? = null
    var prevThread: Thread? = null
    var nextThread: Thread? = null

    companion object {
        var listSongs: ArrayList<MusicFiles>? = ArrayList()
        var uri: Uri? = null
        var mediaPlayer: MediaPlayer? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        getIntentMethod()
        song_name.text = listSongs?.get(position)!!.title
        song_artist.text = listSongs?.get(position)!!.artist

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer!!.seekTo(progress * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        runOnUiThread(object : Runnable {
            override fun run() {
                if (mediaPlayer != null) {
                    var mCurrentPosition = mediaPlayer!!.currentPosition / 1000
                    seekBar.setProgress(mCurrentPosition)
                    durationPlayed.text = (formattedTime(mCurrentPosition))
                }
                handler.postDelayed(this, 1000)
            }
        })

    }

    override fun onResume() {
        playThreadBtn()
        prevThreadBtn()
        nextThreadBtn()
        super.onResume()
    }

    private fun nextThreadBtn() {
        nextThread = Thread() {
            kotlin.run {
                id_next.setOnClickListener {
                    nextBtnClick()
                }
            }
        }
        nextThread?.start()
    }

    private fun nextBtnClick() {
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
                position = ((position + 1) % listSongs!!.size)
                uri = Uri.parse(listSongs!!.get(position).path)
                mediaPlayer = MediaPlayer.create(applicationContext, uri)
                metaData(uri!!)
                song_name.text = listSongs!!.get(position).title
                song_artist.text = listSongs!!.get(position).artist
                seekBar.max = mediaPlayer!!.duration / 1000
                setProgressBar()
                play_pause.setImageResource(R.drawable.ic_baseline_pause_24)
                mediaPlayer!!.start()

            } else {
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
                position = ((position + 1) % listSongs!!.size)
                uri = Uri.parse(listSongs!!.get(position).path)
                mediaPlayer = MediaPlayer.create(applicationContext, uri)
                metaData(uri!!)
                song_name.text = listSongs!!.get(position).title
                song_artist.text = listSongs!!.get(position).artist
                seekBar.max = mediaPlayer!!.duration / 1000
                setProgressBar()
                play_pause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }
        }
    }

    private fun prevThreadBtn() {
        prevThread = Thread() {
            kotlin.run {
                id_prev.setOnClickListener {
                    prevBtnClick()
                }
            }
        }
        prevThread?.start()
    }

    private fun prevBtnClick() {
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
                position = if ((position - 1) < 0) (listSongs!!.size - 1) else (position - 1)
                uri = Uri.parse(listSongs!!.get(position).path)
                mediaPlayer = MediaPlayer.create(applicationContext, uri)
                metaData(uri!!)
                song_name.text = listSongs!!.get(position).title
                song_artist.text = listSongs!!.get(position).artist
                seekBar.max = mediaPlayer!!.duration / 1000
                setProgressBar()
                play_pause.setImageResource(R.drawable.ic_baseline_pause_24)
                mediaPlayer!!.start()

            } else {
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
                position = if ((position - 1) < 0) (listSongs!!.size - 1) else (position - 1)
                uri = Uri.parse(listSongs!!.get(position).path)
                mediaPlayer = MediaPlayer.create(applicationContext, uri)
                metaData(uri!!)
                song_name.text = listSongs!!.get(position).title
                song_artist.text = listSongs!!.get(position).artist
                seekBar.max = mediaPlayer!!.duration / 1000
                setProgressBar()
                play_pause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }
        }
    }

    private fun playThreadBtn() {
        playThread = Thread() {
            kotlin.run {
                play_pause.setOnClickListener {
                    playPauseBtnClick()
                }
            }
        }
        playThread?.start()
    }

    private fun playPauseBtnClick() {
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying) {
                play_pause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                mediaPlayer!!.pause()
                seekBar.max = mediaPlayer!!.duration / 1000
                setProgressBar()

            } else {
                play_pause.setImageResource(R.drawable.ic_baseline_pause_24)
                mediaPlayer!!.start()
                seekBar.max = mediaPlayer!!.duration / 1000
                setProgressBar()
            }
        }
    }

    fun setProgressBar() {
        runOnUiThread(object : Runnable {
            override fun run() {
                if (mediaPlayer != null) {
                    var mCurrentPosition = mediaPlayer!!.currentPosition / 1000
                    seekBar.setProgress(mCurrentPosition)
                }
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun formattedTime(mCurrentPosition: Int): String {
        var totalOut = ""
        var totalNew = ""
        var seconds: String = (mCurrentPosition % 60).toString()
        var minutes: String = (mCurrentPosition / 60).toString()
        totalOut = minutes + ":" + seconds
        totalNew = minutes + ":" + "0" + seconds
        if (seconds.length == 1) {
            return totalNew
        } else {
            return totalOut
        }
    }

    fun metaData(uri: Uri) {
        var retriever: MediaMetadataRetriever = MediaMetadataRetriever()
        retriever.setDataSource(uri.toString())
        var durationtotal = listSongs?.get(position)?.duration!!.toInt() / 1000
        durationTotal.text = formattedTime(durationtotal)
        var art = retriever.embeddedPicture
        if (art != null) {
            Glide.with(this)
                .asBitmap()
                .load(art)
                .into(cover_art)
        } else {
            Glide.with(this)
                .asBitmap()
                .load(R.drawable.ic_launcher_foreground)
                .into(cover_art)
        }
    }


    private fun getIntentMethod() {
        position = intent.getIntExtra("position", -1)
        listSongs = MainActivity.musicFiles
        if (listSongs != null) {
            play_pause.setImageResource(R.drawable.ic_baseline_pause_24)
            uri = Uri.parse(listSongs!!.get(position).path)
            if (mediaPlayer != null) {
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
                mediaPlayer = MediaPlayer.create(applicationContext, uri)
                mediaPlayer!!.start()
            } else {
                mediaPlayer = MediaPlayer.create(applicationContext, uri)
                mediaPlayer!!.start()
            }
            seekBar.max = mediaPlayer!!.duration / 1000
            metaData(uri!!)
        }
    }
}