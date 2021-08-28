package com.grigorevmp.musicplayer

import android.app.Notification
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MusicNotification {

    val channel_play = "actionplay"
    val channel_pause = "actionpause"
    val channel_cycle = "actioncycle"
    val channel_next= "actionnext"

    companion object {
        val CHANNEL_ID = "channel1"
        val NOTIFICATION_ID = 0

        lateinit var notification: Notification

        fun createNotification(context: Context, song: Song, play_button: Int, pos: Int, size: Int){
            val notificationManagerCompat = NotificationManagerCompat.from(context)
            val mediaSessionCompat = MediaSession(context, "tag")

            val icon = BitmapFactory.decodeResource(context.resources, song.image)

            notification= NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music_note)
                .setContentTitle(song.title)
                .setContentText(song.artist)
                .setLargeIcon(icon)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build()

            notificationManagerCompat.notify(NOTIFICATION_ID, notification)
        }
    }
}