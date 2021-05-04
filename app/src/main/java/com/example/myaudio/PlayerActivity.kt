package com.example.myaudio

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_player.*
import java.util.*
import kotlin.collections.ArrayList

class PlayerActivity : AppCompatActivity(), MediaPlayer.OnCompletionListener {

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
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_player)
        getIntentMethod()
        song_name.text = listSongs?.get(position)!!.title
        song_artist.text = listSongs?.get(position)!!.artist
        mediaPlayer!!.setOnCompletionListener(this)
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
        id_shuffle.setOnClickListener {
            if (MainActivity.shuffleBoolean) {
                MainActivity.shuffleBoolean = false
                id_shuffle.setImageResource(R.drawable.ic_shuffle_off)
            } else {
                MainActivity.shuffleBoolean = true
                id_shuffle.setImageResource(R.drawable.ic_baseline_shuffle_on)
            }
        }

        id_repeat.setOnClickListener {
            if (MainActivity.repeatBoolean) {
                MainActivity.repeatBoolean = false
                id_repeat.setImageResource(R.drawable.ic_repeat_off)
            } else {
                MainActivity.repeatBoolean = true
                id_repeat.setImageResource(R.drawable.ic_baseline_repeat__on)
            }
        }
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
                if (MainActivity.shuffleBoolean && !MainActivity.repeatBoolean) {
                    position = getRandom(listSongs!!.size - 1)
                } else if(!MainActivity.shuffleBoolean && !MainActivity.repeatBoolean){
                    position = ((position + 1) % listSongs!!.size)
                }
                uri = Uri.parse(listSongs!!.get(position).path)
                mediaPlayer = MediaPlayer.create(applicationContext, uri)
                metaData(uri!!)
                song_name.text = listSongs!!.get(position).title
                song_artist.text = listSongs!!.get(position).artist
                seekBar.max = mediaPlayer!!.duration / 1000
                setProgressBar()
                mediaPlayer!!.setOnCompletionListener(this)
                play_pause.setBackgroundResource(R.drawable.ic_baseline_pause_24)
                mediaPlayer!!.start()

            } else {
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
                if (MainActivity.shuffleBoolean && !MainActivity.repeatBoolean) {
                    position = getRandom(listSongs!!.size - 1)
                } else if(!MainActivity.shuffleBoolean && !MainActivity.repeatBoolean){
                    position = ((position + 1) % listSongs!!.size)
                }
                uri = Uri.parse(listSongs!!.get(position).path)
                mediaPlayer = MediaPlayer.create(applicationContext, uri)
                metaData(uri!!)
                song_name.text = listSongs!!.get(position).title
                song_artist.text = listSongs!!.get(position).artist
                seekBar.max = mediaPlayer!!.duration / 1000
                setProgressBar()
                mediaPlayer!!.setOnCompletionListener(this)
                play_pause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
            }
        }
    }

    fun getRandom(i: Int): Int {
        var random: Random = Random()
        return random.nextInt(i + 1)
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
                if (MainActivity.shuffleBoolean && !MainActivity.repeatBoolean) {
                    position = getRandom(listSongs!!.size - 1)
                } else if(!MainActivity.shuffleBoolean && !MainActivity.repeatBoolean){
                    position = if ((position - 1) < 0) (listSongs!!.size - 1) else (position - 1)
                }
                uri = Uri.parse(listSongs!!.get(position).path)
                mediaPlayer = MediaPlayer.create(applicationContext, uri)
                metaData(uri!!)
                song_name.text = listSongs!!.get(position).title
                song_artist.text = listSongs!!.get(position).artist
                seekBar.max = mediaPlayer!!.duration / 1000
                setProgressBar()
                mediaPlayer!!.setOnCompletionListener(this)
                play_pause.setBackgroundResource(R.drawable.ic_baseline_pause_24)
                mediaPlayer!!.start()

            } else {
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
                if (MainActivity.shuffleBoolean && !MainActivity.repeatBoolean) {
                    position = getRandom(listSongs!!.size - 1)
                } else if(!MainActivity.shuffleBoolean && !MainActivity.repeatBoolean){
                    position = if ((position - 1) < 0) (listSongs!!.size - 1) else (position - 1)
                }
                uri = Uri.parse(listSongs!!.get(position).path)
                mediaPlayer = MediaPlayer.create(applicationContext, uri)
                metaData(uri!!)
                song_name.text = listSongs!!.get(position).title
                song_artist.text = listSongs!!.get(position).artist
                seekBar.max = mediaPlayer!!.duration / 1000
                setProgressBar()
                mediaPlayer!!.setOnCompletionListener(this)
                play_pause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
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
        var bitmap: Bitmap? = null
        if (art != null) {
//            Glide.with(this)
//                .asBitmap()
//                .load(art)
//                .into(cover_art)


            bitmap = BitmapFactory.decodeByteArray(art, 0, art.size)
            ImageAnimation(this, cover_art, bitmap!!)
            Palette.from(bitmap).generate(object : Palette.PaletteAsyncListener {
                override fun onGenerated(palette: Palette?) {
                    var swatch: Palette.Swatch? = palette?.dominantSwatch
                    if (swatch != null) {
                        var gradient: ImageView = findViewById(R.id.imageViewGradient)
                        var mContainer: RelativeLayout = findViewById(R.id.mContainer)
                        gradient.setBackgroundResource(R.drawable.gradient_bg)
                        mContainer.setBackgroundResource(R.drawable.main_bg)
                        var myArray: IntArray = intArrayOf(swatch.rgb, 0x00000000)
                        var gradientDrawable: GradientDrawable =
                            GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, myArray)
                        gradient.setBackground(gradientDrawable)
                        var myArrayBG: IntArray = intArrayOf(swatch.rgb, swatch.rgb)
                        var gradientDrawableBG: GradientDrawable =
                            GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, myArrayBG)
                        mContainer.setBackground(gradientDrawableBG)
                        song_name.setTextColor(swatch.titleTextColor)
                        song_artist.setTextColor(swatch.bodyTextColor)

                    } else {
                        var gradient: ImageView = findViewById(R.id.imageViewGradient)
                        var mContainer: RelativeLayout = findViewById(R.id.mContainer)
                        gradient.setBackgroundResource(R.drawable.gradient_bg)
                        mContainer.setBackgroundResource(R.drawable.main_bg)
                        var myArray: IntArray = intArrayOf(0xff000000.toInt(), 0x00000000)
                        var gradientDrawable: GradientDrawable =
                            GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, myArray)
                        gradient.setBackground(gradientDrawable)
                        var myArrayBG: IntArray = intArrayOf(0xff000000.toInt(), 0xff000000.toInt())
                        var gradientDrawableBG: GradientDrawable =
                            GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, myArrayBG)
                        mContainer.setBackground(gradientDrawableBG)
                        song_name.setTextColor(Color.WHITE)
                        song_artist.setTextColor(Color.DKGRAY)
                    }
                }

            })
        } else {
            Glide.with(this)
                .asBitmap()
                .load(R.drawable.ic_launcher_foreground)
                .into(cover_art)
            var gradient: ImageView = findViewById(R.id.imageViewGradient)
            var mContainer: RelativeLayout = findViewById(R.id.mContainer)
            gradient.setBackgroundResource(R.drawable.gradient_bg)
            mContainer.setBackgroundResource(R.drawable.main_bg)
            song_name.setTextColor(Color.WHITE)
            song_artist.setTextColor(Color.DKGRAY)
        }
    }


    private fun getIntentMethod() {
        position = intent.getIntExtra("position", -1)
        var sender: String? = intent.getStringExtra("sender")
        if(sender != null && sender.equals("albumDetails")){
            listSongs = AlbumDetailsAdapter.albumFiles
        } else{
            listSongs = MusicAdapter.mFiles
        }
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

    public fun ImageAnimation(context: Context, imageView: ImageView, bitmap: Bitmap) {

        var animOut: Animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)
        var animIn: Animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
        animOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                Glide.with(context).load(bitmap).into(imageView)
                animIn.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {

                    }

                    override fun onAnimationEnd(animation: Animation?) {

                    }

                    override fun onAnimationStart(animation: Animation?) {

                    }

                })
                imageView.startAnimation(animIn)
            }

            override fun onAnimationStart(animation: Animation?) {

            }

        })
        imageView.startAnimation(animOut)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        nextBtnClick()
        if (mediaPlayer != null) {
            mediaPlayer = MediaPlayer.create(applicationContext, uri)
            mediaPlayer!!.start()
            mediaPlayer!!.setOnCompletionListener(this)
        }
    }
}