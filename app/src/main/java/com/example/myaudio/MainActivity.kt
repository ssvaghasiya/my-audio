package com.example.myaudio

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.icu.text.CaseMap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private val STORAGE_PERMISSION_CODE = 101
    private val My_SORT_REF = "SortOrder"

    companion object {
        var musicFiles: ArrayList<MusicFiles>? = ArrayList()
        var shuffleBoolean: Boolean = false
        var repeatBoolean: Boolean = false
        var albums: ArrayList<MusicFiles>? = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewPager()

        if (checkPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                STORAGE_PERMISSION_CODE
            )
        ) {
            musicFiles = getAllAudio(this)
        } else {
            ActivityCompat
                .requestPermissions(
                    this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_CODE
                )
        }
    }

    private fun initViewPager() {
        var viewPagerAdapter: ViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragments(SongsFragment(), "Songs")
        viewPagerAdapter.addFragments(AlbumFragment(), "Albums")
        viewpager.adapter = viewPagerAdapter
        tab_layout.setupWithViewPager(viewpager)
    }

    fun getAllAudio(context: Context): ArrayList<MusicFiles> {

        var preferences: SharedPreferences = getSharedPreferences(My_SORT_REF, Context.MODE_PRIVATE)
        var sortOrder: String? = preferences.getString("sorting","sortByName")

        var duplicate: ArrayList<String> = ArrayList()
        albums?.clear()
        var tempAudioList: ArrayList<MusicFiles> = ArrayList()
        var order: String? = null
        var uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        when (sortOrder) {
            "sortByName" -> {
                    order  = MediaStore.MediaColumns.DISPLAY_NAME + " ASC"
            }
            "sortByDate" -> {
                order  = MediaStore.MediaColumns.DATE_ADDED + " ASC"
            }
            "sortBySize" -> {
                order  = MediaStore.MediaColumns.SIZE + " DESC"
            }
        }
        val projection = arrayOf<String>(
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media._ID
        )

        var cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, order)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                var album = cursor.getString(0)
                var title = cursor.getString(1)
                var duration = cursor.getString(2)
                var path = cursor.getString(3)
                var artist = cursor.getString(4)
                var id = cursor.getString(5)

                var musicFiles: MusicFiles = MusicFiles(path, title, artist, album, duration, id)
                tempAudioList.add(musicFiles)
                if (!duplicate.contains(album)) {
                    albums?.add(musicFiles)
                    duplicate.add(album)
                }
                Log.e("Songs", title + "  " + path)
            }

            cursor.close()
        }
        return tempAudioList
    }

    // Function to check and request permission
    fun checkPermission(permission: String, requestCode: Int): Boolean {

        // Checking if permission is not granted
        return ContextCompat.checkSelfPermission(
            this@MainActivity,
            permission
        ) != PackageManager.PERMISSION_DENIED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super
            .onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    this@MainActivity,
                    "Storage Permission Granted",
                    Toast.LENGTH_SHORT
                )
                    .show()
                musicFiles = getAllAudio(this)
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Storage Permission Denied",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    class ViewPagerAdapter : FragmentPagerAdapter {

        var fragments: ArrayList<Fragment>? = null
        var titles: ArrayList<String>? = null

        constructor(fm: FragmentManager) : super(fm) {
            fragments = ArrayList()
            titles = ArrayList()
        }

        fun addFragments(fragment: Fragment, title: String) {
            fragments?.add(fragment)
            titles?.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return fragments!!.get(position)
        }

        override fun getCount(): Int {
            return fragments!!.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles?.get(position)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        var menuItem: MenuItem = menu!!.findItem(R.id.search_option)
        var searchView: SearchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        var userInput: String = newText!!.toLowerCase()
        var myFiles = ArrayList<MusicFiles>()
        for (song in musicFiles!!) {
            if (song.title!!.toLowerCase().contains(userInput)) {
                myFiles.add(song)
            }
        }
        SongsFragment.musicAdapter!!.updateList(myFiles)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var editor: SharedPreferences.Editor =
            getSharedPreferences(My_SORT_REF, Context.MODE_PRIVATE).edit()
        when (item.itemId) {
            R.id.by_name -> {
                editor.putString("sorting", "sortByName")
                editor.apply()
                this.recreate()
            }
            R.id.by_date -> {
                editor.putString("sorting", "sortByDate")
                editor.apply()
                this.recreate()
            }
            R.id.by_size -> {
                editor.putString("sorting", "sortBySize")
                editor.apply()
                this.recreate()
            }
        }
        true

        return super.onOptionsItemSelected(item)

    }
}