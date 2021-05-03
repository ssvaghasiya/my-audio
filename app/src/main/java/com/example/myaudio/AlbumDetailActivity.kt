package com.example.myaudio

import android.media.MediaMetadataRetriever
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AlbumDetailActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var albumPhoto: ImageView
    var albumName: String? = null
    var albumSongs: ArrayList<MusicFiles>? = ArrayList()
    var albumDetailsAdapter: AlbumDetailsAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_detail)
        recyclerView = findViewById(R.id.recycleViewAD)
        albumPhoto = findViewById(R.id.albumPhoto)
        albumName = intent.getStringExtra("albumName")
        var j = 0;
        for (i in 0 until MainActivity.musicFiles!!.size) {
            if (albumName.equals(MainActivity.musicFiles!!.get(i).album)) {
                albumSongs?.add(j, MainActivity.musicFiles!!.get(i))
                j++
            }
        }

        var image = albumSongs?.get(0)?.path?.let { getAlbumArt(it) }
        if (image != null) {
            Glide.with(this).asBitmap()
                .load(image)
                .into(albumPhoto)
        } else {
            Glide.with(this)
                .load(R.drawable.ic_launcher_foreground)
                .into(albumPhoto)
        }
    }

    override fun onResume() {
        super.onResume()
        if(!(albumSongs!!.size < 1)){
            albumDetailsAdapter = AlbumDetailsAdapter(this,albumSongs)
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = albumDetailsAdapter
            recyclerView.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)

        }
    }

    fun getAlbumArt(uri: String): ByteArray? {
        var retriever: MediaMetadataRetriever = MediaMetadataRetriever()
        retriever.setDataSource(uri.toString())
        var art = retriever.embeddedPicture
        retriever.release()
        return art
    }
}