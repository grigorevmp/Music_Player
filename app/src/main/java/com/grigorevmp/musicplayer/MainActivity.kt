package com.grigorevmp.musicplayer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private var isMusicPlaying = false
    lateinit var controlMusic: FloatingActionButton

    lateinit var notificationManager: NotificationManager

    val songs: MutableList<Song> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        controlMusic = findViewById(R.id.control_music)

        loadSongs()

        createChannel()

        controlMusic.setOnClickListener {
            musicControl()
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

    private fun musicControl() {
        if (isMusicPlaying) {
            notificationDestroyer()
        } else {
            notificationBuilder()
        }
    }

    private fun notificationBuilder(){
        toast("Music started")
        controlMusic.setImageResource(R.drawable.ic_pause)
        MusicNotification.createNotification(this, songs[0],
        R.drawable.ic_pause, 0, songs.size - 1)
        isMusicPlaying = true
    }

    private fun notificationDestroyer(){
        toast("Music stopped")
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(0)
        controlMusic.setImageResource(R.drawable.ic_play)
        isMusicPlaying = false
    }

    private fun toast(str: String){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
    }
}