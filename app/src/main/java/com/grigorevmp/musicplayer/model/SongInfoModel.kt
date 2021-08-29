package com.grigorevmp.musicplayer.model

import com.grigorevmp.musicplayer.R

class SongInfoModel {
    private val songs: MutableList<SongInfo> = arrayListOf()

    fun loadSongs(): MutableList<SongInfo> {
        songs.add(SongInfo("Song 1", "Artist 1", R.drawable.cover_1))
        songs.add(SongInfo("Song 2", "Artist 2", R.drawable.cover_2))
        songs.add(SongInfo("Song 3", "Artist 3", R.drawable.cover_3))
        songs.add(SongInfo("Song 4", "Artist 4", R.drawable.cover_4))

        return songs
    }
}