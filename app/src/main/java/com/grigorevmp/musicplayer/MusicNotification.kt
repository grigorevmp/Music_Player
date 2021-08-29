package com.grigorevmp.musicplayer

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.grigorevmp.musicplayer.model.SongInfo


class MusicNotification {

    companion object {
        val CHANNEL_ID = "channel1"
        val NOTIFICATION_ID = 0
        val ACTION_PREVIOUS = "action_previous"
        val ACTION_NEXT = "action_next"
        val ACTION_PLAY = "action_play"
        val ACTION_REPEAT = "action_repeat"
        val ACTION_RANDOM = "action_random"

        lateinit var notification: Notification

        fun createNotification(
            context: Context,
            song: SongInfo,
            play_button: Int,
            pos: Int,
            size: Int,
            songDuration: Int,
            looping: Boolean = false
        ) {
            val notificationManagerCompat = NotificationManagerCompat.from(context)


            val builder = MediaMetadataCompat.Builder()
            builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, songDuration.toLong())


            val playbackStateCompat = PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE or
                            PlaybackStateCompat.ACTION_FAST_FORWARD or PlaybackStateCompat.ACTION_REWIND or
                            PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or PlaybackStateCompat.ACTION_PAUSE or
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or PlaybackStateCompat.ACTION_SEEK_TO
                )
                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 0f, 0)
                .build()


            val mediaSessionCompat = MediaSessionCompat(context, "tag")
            mediaSessionCompat.setPlaybackState(playbackStateCompat)
            mediaSessionCompat.setMetadata(builder.build())


            val icon = BitmapFactory.decodeResource(context.resources, song.image)
            val pendingIntentPrevious: PendingIntent?
            val pendingIntentNext: PendingIntent?
            val iconPrevious: Int
            val iconNext: Int

            when (pos) {
                0 -> {
                    pendingIntentPrevious = null
                    iconPrevious = 0
                    val intentNext = Intent(
                        context,
                        NotificationActionService::class.java
                    ).setAction(ACTION_NEXT)
                    pendingIntentNext = PendingIntent.getBroadcast(
                        context,
                        0,
                        intentNext,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    iconNext = R.drawable.ic_skip_next
                }
                size -> {
                    iconNext = 0
                    pendingIntentNext = null
                    val intentPrevious = Intent(
                        context,
                        NotificationActionService::class.java
                    ).setAction(ACTION_PREVIOUS)
                    pendingIntentPrevious = PendingIntent.getBroadcast(
                        context,
                        0,
                        intentPrevious,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    iconPrevious = R.drawable.ic_skip_prev
                }
                else -> {
                    val intentPrevious = Intent(
                        context,
                        NotificationActionService::class.java
                    ).setAction(ACTION_PREVIOUS)

                    pendingIntentPrevious = PendingIntent.getBroadcast(
                        context,
                        0,
                        intentPrevious,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )

                    iconPrevious = R.drawable.ic_skip_prev

                    val intentNext = Intent(
                        context,
                        NotificationActionService::class.java
                    ).setAction(ACTION_NEXT)

                    pendingIntentNext = PendingIntent.getBroadcast(
                        context,
                        0,
                        intentNext,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    iconNext = R.drawable.ic_skip_next
                }
            }

            val intentPlay = Intent(
                context,
                NotificationActionService::class.java
            )
                .setAction(ACTION_PLAY)
            val pendingIntentPlay = PendingIntent.getBroadcast(
                context,
                0,
                intentPlay,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val intentRepeat = Intent(
                context,
                NotificationActionService::class.java
            )
                .setAction(ACTION_REPEAT)
            val pendingIntentRepeat = PendingIntent.getBroadcast(
                context,
                0,
                intentRepeat,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val iconRepeat: Int = if (looping)
                R.drawable.ic_repeat_one
            else
                R.drawable.ic_repeat


            val intentRandom = Intent(
                context,
                NotificationActionService::class.java
            )
                .setAction(ACTION_RANDOM)
            val pendingIntentRandom = PendingIntent.getBroadcast(
                context,
                0,
                intentRandom,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val iconRandom: Int = R.drawable.ic_random




            notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music_note)
                .setContentTitle(song.title)
                .setContentText(song.artist)
                .setLargeIcon(icon)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .addAction(iconPrevious, "Previous", pendingIntentPrevious)
                .addAction(play_button, "Play", pendingIntentPlay)
                .addAction(iconNext, "Next", pendingIntentNext)
                .addAction(iconRepeat, "Repeat", pendingIntentRepeat)
                .addAction(iconRandom, "Random", pendingIntentRandom)
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(1, 2, 3, 4, 5)
                    .setMediaSession(mediaSessionCompat.sessionToken)
                )
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build()

            notificationManagerCompat.notify(NOTIFICATION_ID, notification)
        }
    }
}