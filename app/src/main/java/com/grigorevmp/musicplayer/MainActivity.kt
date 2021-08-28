package com.grigorevmp.musicplayer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity(), Playable {
    lateinit var controlMusic: FloatingActionButton

    lateinit var mediaPlayer: MediaPlayer

    lateinit var notificationManager: NotificationManager

    private val songs: MutableList<Song> = arrayListOf()

    private var position = 0
    private var isPlaying = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        controlMusic = findViewById(R.id.control_music)

        loadSongs()
        createChannel()
        registerReceiver(broadCastReceiver, IntentFilter("SONGS"))
        startService(Intent(baseContext, OnClearFromRecentService::class.java))

        controlMusic.setOnClickListener {
            if (isPlaying) {
                onTrackPause()
            } else {
                onTrackPlay()
            }
        }

    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel: NotificationChannel =
                NotificationChannel(
                    MusicNotification.CHANNEL_ID,
                    "Mini player",
                    NotificationManager.IMPORTANCE_HIGH
                )
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }

    private fun loadSongs() {
        songs.add(Song("Song 1", "Artist 1", R.drawable.cover_1))
        songs.add(Song("Song 2", "Artist 2", R.drawable.cover_2))
        songs.add(Song("Song 3", "Artist 3", R.drawable.cover_3))
        songs.add(Song("Song 4", "Artist 4", R.drawable.cover_4))
    }


    private fun toast(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
    }

    open class MusicBroadcastReceiver(function: () -> Unit) : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.extras!!.getString("actionname")

        }
    }

    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            when (intent?.action) {
                MusicNotification.ACTION_PREVIOUS -> onTrackPrevious()
                MusicNotification.ACTION_PLAY -> {
                    if (isPlaying)
                        onTrackPause()
                    else
                        onTrackPlay()
                }
                MusicNotification.ACTION_NEXT -> onTrackNext()
                MusicNotification.ACTION_REPEAT -> onTrackRepeat()
            }
        }
    }


    override fun onTrackPrevious() {
        position -= 1
        MusicNotification.createNotification(
            this,
            songs[position],
            R.drawable.ic_pause,
            0,
            songs.size - 1
        )
    }

    override fun onTrackPlay() {
        MusicNotification.createNotification(
            this,
            songs[position],
            R.drawable.ic_pause,
            0,
            songs.size - 1
        )
        isPlaying = true
        controlMusic.setImageResource(R.drawable.ic_pause)
    }

    override fun onTrackPause() {
        MusicNotification.createNotification(
            this,
            songs[position],
            R.drawable.ic_pause,
            0,
            songs.size - 1
        )
        isPlaying = false
        controlMusic.setImageResource(R.drawable.ic_play)
    }

    override fun onTrackNext() {
        position += 1
        MusicNotification.createNotification(
            this,
            songs[position],
            R.drawable.ic_pause,
            0,
            songs.size - 1
        )
    }

    override fun onTrackRepeat() {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager.cancelAll()
        unregisterReceiver(broadCastReceiver)
    }
}