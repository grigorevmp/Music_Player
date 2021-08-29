package com.grigorevmp.musicplayer.viewModel

import androidx.lifecycle.MutableLiveData
import com.grigorevmp.musicplayer.model.*

class MediaViewModel {
    private var liveSongs: MutableLiveData<List<Song>>? = null
    private var liveCurrent: MutableLiveData<Song>? = null
    private val songModel = SongModel()

    private val songInfoModel = SongInfoModel().loadSongs()

    private fun loadSongInfo(position: Int): SongInfo {
        return songInfoModel[position]
    }

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

    fun createMusicNotification(position: Int, max_position: Int,
                                play: Boolean = false,
                                looping: Boolean = false) {
        MusicNotificationModel.create(loadSongInfo(position), position, max_position, play, looping = looping)
    }
}


