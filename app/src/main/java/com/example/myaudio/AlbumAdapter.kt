package com.example.myaudio

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import java.io.File

class AlbumAdapter : RecyclerView.Adapter<AlbumAdapter.MyViewHolder> {

    var context: Context? = null
    var albumFiles: ArrayList<MusicFiles>? = ArrayList()

    constructor(context: Context, mFiles: ArrayList<MusicFiles>?) {
        this.context = context
        this.albumFiles = mFiles
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.album_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return albumFiles!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.album_name?.text = albumFiles?.get(position)?.album
        var image = getAlbumArt(albumFiles?.get(position)?.path!!)
        if (image != null) {
            Glide.with(context!!).asBitmap()
                .load(image)
                .into(holder.album_image!!)
        } else {
            Glide.with(context!!)
                .load(R.drawable.ic_launcher_foreground)
                .into(holder.album_image!!)
        }
        holder.itemView.setOnClickListener {
            val i = Intent(context, AlbumDetailActivity::class.java)
            i.putExtra("albumName", albumFiles!!.get(position).album)
            context!!.startActivity(i)
        }

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var album_name: TextView? = itemView.findViewById(R.id.album_name)
        var album_image: ImageView? = itemView.findViewById(R.id.album_img)
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