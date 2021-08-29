package com.grigorevmp.musicplayer.view

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
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.grigorevmp.musicplayer.MusicNotification
import com.grigorevmp.musicplayer.OnClearFromRecentService
import com.grigorevmp.musicplayer.Playable
import com.grigorevmp.musicplayer.R
import com.grigorevmp.musicplayer.model.SongInfo
import com.grigorevmp.musicplayer.model.SongInfoModel
import com.grigorevmp.musicplayer.model.SongNames
import com.grigorevmp.musicplayer.viewModel.MediaViewModel
import kotlin.random.Random


class MainActivity : AppCompatActivity(), Playable {
    private lateinit var controlMusic: FloatingActionButton

    private var mediaPlayer: MediaPlayer = MediaPlayer()

    private lateinit var notificationManager: NotificationManager

    private val songs: MutableList<SongInfo> = SongInfoModel().loadSongs()

    private var position = 0
    private val songsNames: MutableList<String> = SongNames.data
    private val maxPosition = songsNames.size - 1

    private var mediaViewModel = MediaViewModel()

    private var isPlaying = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        controlMusic = findViewById(R.id.control_music)

        initMediaPlayer(position)

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

        mediaPlayer.setOnCompletionListener {
            if (position == maxPosition) {
                position = 0
                onTrackChange()
            } else {
                position += 1
                onTrackChange()
            }
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    MusicNotification.CHANNEL_ID,
                    "Mini player",
                    NotificationManager.IMPORTANCE_HIGH
                )
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }

    private fun initMediaPlayer(position: Int, change: Boolean = false){
        if(change){
            mediaPlayer.stop()
            mediaPlayer.reset()
        }

        val song = mediaViewModel.getSongByPosition(position)
        mediaPlayer.setDataSource(
            song.songDescriptor.fileDescriptor,
            song.songDescriptor.startOffset,
            song.songDescriptor.length
        )
        song.songDescriptor.close()

        mediaPlayer.prepare()
        mediaPlayer.setVolume(1f, 1f)
        mediaPlayer.isLooping = false
    }

    private fun changeSongLooping(){
        mediaPlayer.isLooping = !mediaPlayer.isLooping
    }

    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            when (intent?.extras?.getString("action_name")) {
                MusicNotification.ACTION_PREVIOUS -> onTrackPrevious()
                MusicNotification.ACTION_PLAY -> {
                    if (isPlaying)
                        onTrackPause()
                    else
                        onTrackPlay()
                }
                MusicNotification.ACTION_NEXT -> onTrackNext()
                MusicNotification.ACTION_REPEAT -> onTrackRepeat()
                MusicNotification.ACTION_RANDOM -> onTrackRandom()
            }
        }
    }

    override fun onTrackPrevious() {
        position -= 1
        initMediaPlayer(position, change = true)
        mediaPlayer.start()

        MusicNotification.createNotification(
            this,
            songs[position],
            R.drawable.ic_pause,
            position,
            songs.size - 1,
            mediaPlayer.duration
        )
    }

    override fun onTrackPlay() {
        isPlaying = true
        controlMusic.setImageResource(R.drawable.ic_pause)
        mediaPlayer.start()

        MusicNotification.createNotification(
            this,
            songs[position],
            R.drawable.ic_pause,
            position,
            songs.size - 1,
            mediaPlayer.duration
        )
    }

    override fun onTrackChange() {
        initMediaPlayer(position, change = true)
        mediaPlayer.start()

        MusicNotification.createNotification(
            this,
            songs[position],
            R.drawable.ic_pause,
            position,
            songs.size - 1,
            mediaPlayer.duration
        )
    }


    override fun onTrackPause() {
        isPlaying = false
        controlMusic.setImageResource(R.drawable.ic_play)
        mediaPlayer.pause()

        MusicNotification.createNotification(
            this,
            songs[position],
            R.drawable.ic_play,
            position,
            songs.size - 1,
            mediaPlayer.duration
        )
    }

    override fun onTrackRandom() {
        initMediaPlayer(position, change = true)
        mediaPlayer.start()

        val randomPosition = Random.nextInt(songs.size - 1)
        position = randomPosition
        MusicNotification.createNotification(
            this,
            songs[position],
            R.drawable.ic_pause,
            position,
            songs.size - 1,
            mediaPlayer.duration
        )
    }

    override fun onTrackNext() {
        initMediaPlayer(position, change = true)
        mediaPlayer.start()

        position += 1
        MusicNotification.createNotification(
            this,
            songs[position],
            R.drawable.ic_pause,
            position,
            songs.size - 1,
            mediaPlayer.duration
        )
    }

    override fun onTrackRepeat() {
        changeSongLooping()
        MusicNotification.createNotification(
            this,
            songs[position],
            R.drawable.ic_pause,
            position,
            songs.size - 1,
            mediaPlayer.duration,
            looping = mediaPlayer.isLooping
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager.cancelAll()
        unregisterReceiver(broadCastReceiver)
    }
}