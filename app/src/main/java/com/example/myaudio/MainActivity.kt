package com.example.myaudio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.icu.text.CaseMap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private val STORAGE_PERMISSION_CODE = 101
    companion object {
        var musicFiles: ArrayList<MusicFiles>? = null
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
        var tempAudioList: ArrayList<MusicFiles> = ArrayList()
        var uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf<String>(
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ARTIST
        )

        var cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                var album = cursor.getString(0)
                var title = cursor.getString(1)
                var duration = cursor.getString(2)
                var path = cursor.getString(3)
                var artist = cursor.getString(4)

                var musicFiles: MusicFiles = MusicFiles(path, title, artist, album, duration)
                tempAudioList.add(musicFiles)
                Log.e("Songs",title+"  "+path)
            }

            cursor.close()
        }
        return tempAudioList
    }

    // Function to check and request permission
    fun checkPermission(permission: String, requestCode: Int): Boolean {

        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                permission
            )
            == PackageManager.PERMISSION_DENIED
        ) {
            return false
        } else {
            Toast
                .makeText(
                    this@MainActivity,
                    "Permission already granted",
                    Toast.LENGTH_SHORT
                )
                .show()
            return true
        }
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
}