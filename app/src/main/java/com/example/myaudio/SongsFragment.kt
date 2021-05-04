package com.example.myaudio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_songs.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SongsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SongsFragment : Fragment() {

    var recyclerView: RecyclerView? = null

    companion object {
        var musicAdapter: MusicAdapter? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_songs, container, false)
        recyclerView = view.findViewById<View>(R.id.recycleView) as RecyclerView
        recyclerView?.setHasFixedSize(true)
        if (!(MainActivity.musicFiles!!.size < 1)) {
            musicAdapter = MusicAdapter(context!!, MainActivity.musicFiles)
            recyclerView?.adapter = musicAdapter

        }
        return view
    }
}