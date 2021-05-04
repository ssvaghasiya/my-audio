package com.example.myaudio

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.view.Gravity
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

class MusicAdapter : RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    var context: Context? = null

    companion object {
        var mFiles: ArrayList<MusicFiles>? = ArrayList()
    }


    constructor(context: Context, mFiles: ArrayList<MusicFiles>?) {
        this.context = context
        MusicAdapter.mFiles = mFiles
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
        } else {
            Glide.with(context!!)
                .load(R.drawable.ic_launcher_foreground)
                .into(holder.album_art!!)
        }
        holder.itemView.setOnClickListener {
            val i = Intent(context, PlayerActivity::class.java)
            i.putExtra("position", position)
            context!!.startActivity(i)
        }

        holder.menuMore!!.setOnClickListener {
            try {
                val popupMenu: PopupMenu = PopupMenu(context, it)
                popupMenu.menuInflater.inflate(R.menu.popup, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.delete -> {
                            deleteFile(position, it)
                        }
                    }
                    true
                })
                popupMenu.show()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun deleteFile(position: Int, view: View?) {
        var contentUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            mFiles!!.get(position).id!!.toLong()
        )  //  content://
        var file: File = File(mFiles!!.get(position).path)
        var deleted: Boolean = file.delete()
        if (deleted) {
            context!!.contentResolver.delete(contentUri, null, null)
            mFiles!!.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, mFiles!!.size)
            Snackbar.make(view!!, "File Deleted", Snackbar.LENGTH_LONG).show()
        } else {

            //may  be in sd card
            Snackbar.make(view!!, "CAn't be Deleted", Snackbar.LENGTH_LONG).show()

        }
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var file_name: TextView? = itemView.findViewById(R.id.music_file_name)
        var album_art: ImageView? = itemView.findViewById(R.id.music_img)
        var menuMore: ImageView? = itemView.findViewById(R.id.menuMore)
    }

    fun getAlbumArt(uri: String): ByteArray? {
        var retriever: MediaMetadataRetriever = MediaMetadataRetriever()
        retriever.setDataSource(uri.toString())
        var art = retriever.embeddedPicture
        retriever.release()
        return art
    }

    fun updateList(musicFilesArrayList: ArrayList<MusicFiles>){
        mFiles = ArrayList()
        mFiles!!.addAll(musicFilesArrayList)
        notifyDataSetChanged()
    }

}