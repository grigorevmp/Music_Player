package com.grigorevmp.musicplayer.viewModel

import androidx.lifecycle.MutableLiveData
import com.grigorevmp.musicplayer.model.Song
import com.grigorevmp.musicplayer.model.SongModel

class MediaViewModel {
    private var liveSongs: MutableLiveData<List<Song>>? = null
    private var liveCurrent: MutableLiveData<Song>? = null
    private val songModel = SongModel()

    fun loadSongs(): List<Song> {
        val songs = songModel.loadSongs()
        liveSongs?.postValue(songs)
        return songs
    }

    fun getSongByPosition(position: Int): Song {
        val song = songModel.loadSongById(position)
        liveCurrent?.postValue(song)
        return song
    }
}


