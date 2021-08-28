package com.grigorevmp.musicplayer

interface Playable {
    fun onTrackPrevious()
    fun onTrackPlay()
    fun onTrackPause()
    fun onTrackNext()
    fun onTrackRepeat()
}