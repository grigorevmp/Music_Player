package com.grigorevmp.musicplayer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity(), Playable {
    private lateinit var controlMusic: FloatingActionButton

    var mediaPlayer: MediaPlayer = MediaPlayer()

    lateinit var notificationManager: NotificationManager

    private val songs: MutableList<Song> = arrayListOf()

    private val songsNames: MutableList<String> = arrayListOf(
        "songs/song_1.mp3",
        "songs/song_2.mp3",
        "songs/song_3.mp3",
        "songs/song_4.mp3"
    )

    private var position = 0
    private var isPlaying = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        controlMusic = findViewById(R.id.control_music)

        playSong(position)

        loadSongs()
        createChannel()
        registerReceiver(broadCastReceiver, IntentFilter("Songs"))
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

        }
    }

    private fun playSong(position: Int){
        val descriptor: AssetFileDescriptor = this.assets.openFd(songsNames[position])
        mediaPlayer.setDataSource(
            descriptor.fileDescriptor,
            descriptor.startOffset,
            descriptor.length
        )
        descriptor.close()

        mediaPlayer.prepare()
        mediaPlayer.setVolume(1f, 1f)
        mediaPlayer.isLooping = false
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


    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            val action = intent?.extras?.getString("actionname")
            when (action) {
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
        mediaPlayer.stop()
        mediaPlayer.reset()
        playSong(position)
        mediaPlayer.start()
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
        mediaPlayer.start()
    }

    override fun onTrackPause() {
        MusicNotification.createNotification(
            this,
            songs[position],
            R.drawable.ic_play,
            0,
            songs.size - 1
        )
        isPlaying = false
        controlMusic.setImageResource(R.drawable.ic_play)
        mediaPlayer.pause()
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
        mediaPlayer.stop()
        mediaPlayer.reset()
        playSong(position)
        mediaPlayer.start()
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