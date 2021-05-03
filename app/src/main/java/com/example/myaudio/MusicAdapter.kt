package com.example.myaudio

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MusicAdapter : RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    var context: Context? = null
    var mFiles: ArrayList<MusicFiles>? = ArrayList()

    constructor(context: Context, mFiles: ArrayList<MusicFiles>?) {
        this.context = context
        this.mFiles = mFiles
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.music_items, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mFiles!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.file_name?.text = mFiles?.get(position)?.title
        var image = getAlbumArt(mFiles?.get(position)?.path!!)
        if (image != null) {
            Glide.with(context!!).asBitmap()
                .load(image)
                .into(holder.album_art!!)
        } else{
            Glide.with(context!!)
                .load(R.drawable.ic_launcher_background)
                .into(holder.album_art!!)
        }
        holder.itemView.setOnClickListener {
            val i = Intent(context, PlayerActivity::class.java)
            i.putExtra("position",position)
            context!!.startActivity(i)
        }
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var file_name: TextView? = itemView.findViewById(R.id.music_file_name)
        var album_art: ImageView? = itemView.findViewById(R.id.music_img)
    }

    fun getAlbumArt(uri: String): ByteArray? {
        var retriever: MediaMetadataRetriever = MediaMetadataRetriever()
        retriever.setDataSource(uri.toString())
        var art = retriever.embeddedPicture
        retriever.release()
        return art
    }

}