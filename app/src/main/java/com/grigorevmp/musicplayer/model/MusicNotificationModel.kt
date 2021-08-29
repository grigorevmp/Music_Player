package com.grigorevmp.musicplayer.model

import com.grigorevmp.musicplayer.App
import com.grigorevmp.musicplayer.MusicNotification
import com.grigorevmp.musicplayer.R

class MusicNotificationModel {
    companion object {
        fun create(songInfo: SongInfo, position: Int, max_position: Int,
                   play: Boolean = false,
                   looping:Boolean = false) {


            val icon = if (play) {
                R.drawable.ic_play
            } else {
                R.drawable.ic_pause
            }

            MusicNotification.createNotification(
                App.applicationContext(),
                songInfo,
                icon,
                position,
                max_position,
                looping = looping
            )
        }

    }
}