package com.grigorevmp.musicplayer

import android.app.Notification
import android.content.Context

class MusicNotification {
    val CHANNEL_ID = "channel1"

    val channel_play = "actionplay"
    val channel_pause = "actionpause"
    val channel_cycle = "actioncycle"
    val channel_next= "actionnext"

    companion object {
        lateinit var notification: Notification

        fun createNotification(context: Context){

        }
    }
}