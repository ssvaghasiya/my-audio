package com.example.myaudio

class MusicFiles {

    var path: String? = null
    var title: String? = null
    var artist: String? = null
    var album: String? = null
    var duration: String? = null
    var id: String? = null

    constructor(path: String, title: String, artist: String, album: String, duration: String, id: String){
        this.path = path
        this.title = title
        this.artist = artist
        this.album = album
        this.duration = duration
        this.id = id

    }

}