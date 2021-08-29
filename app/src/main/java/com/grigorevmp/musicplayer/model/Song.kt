package com.grigorevmp.musicplayer.model

import android.content.res.AssetFileDescriptor
import com.grigorevmp.musicplayer.App

class SongNames {
    companion object {
        val data: MutableList<String> = arrayListOf(
            "songs/song_1.mp3",
            "songs/song_2.mp3",
            "songs/song_3.mp3",
            "songs/song_4.mp3"
        )
    }
}


data class Song(
    val songName: String,
    val songDescriptor: AssetFileDescriptor
)

class SongModel {
    private val songsNames = SongNames.data
    fun loadSongById(position: Int): Song {
        return Song(
            songsNames[position],
            App.applicationContext().assets.openFd(songsNames[position])
        )
    }

    fun loadSongs(): List<Song> {
        val songDescriptors: MutableList<Song> = arrayListOf()
        for (songName in songsNames) {
            songDescriptors.add(
                Song(
                    songName,
                    App.applicationContext().assets.openFd(songName)
                )
            )
        }
        return songDescriptors
    }
}

