package com.grigorevmp.musicplayer.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.grigorevmp.musicplayer.MusicNotification
import com.grigorevmp.musicplayer.service.OnClearFromRecentService
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
    private var isLooping = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        controlMusic = findViewById(R.id.control_music)

        initMediaPlayer(position)

        createChannel()
        registerReceiver(
            broadCastReceiver,
            IntentFilter(
                "Songs action"
            )
        )
        startService(
            Intent(
                baseContext,
                OnClearFromRecentService::class.java
            )
        )

        controlMusic.setOnClickListener {
            if (isPlaying) {
                onTrackPause()
            } else {
                onTrackPlay()
            }
        }

        mediaPlayer.setOnCompletionListener {
            position = (position + 1) % maxPosition
            onTrackChange()
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
        mediaPlayer.isLooping = isLooping
    }

    private fun changeSongLooping(){
        isLooping = !mediaPlayer.isLooping
        mediaPlayer.isLooping = isLooping
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

        mediaViewModel.createMusicNotification(position, maxPosition, looping = isLooping)

    }

    override fun onTrackPlay() {
        isPlaying = true
        controlMusic.setImageResource(R.drawable.ic_pause)
        mediaPlayer.start()

        mediaViewModel.createMusicNotification(position, maxPosition, looping = isLooping)
    }

    override fun onTrackChange() {
        initMediaPlayer(position, change = true)
        mediaPlayer.start()

        mediaViewModel.createMusicNotification(position, maxPosition, looping = isLooping)
    }


    override fun onTrackPause() {
        isPlaying = false
        controlMusic.setImageResource(R.drawable.ic_play)
        mediaPlayer.pause()

        mediaViewModel.createMusicNotification(position, maxPosition, true, looping = isLooping)
    }

    override fun onTrackRandom() {
        initMediaPlayer(position, change = true)
        mediaPlayer.start()

        val randomPosition = Random.nextInt(songs.size - 1)
        position = randomPosition

        mediaViewModel.createMusicNotification(position, maxPosition, looping = isLooping)
    }

    override fun onTrackNext() {
        position += 1
        initMediaPlayer(position, change = true)
        mediaPlayer.start()

        mediaViewModel.createMusicNotification(position, maxPosition, looping = isLooping)
    }

    override fun onTrackRepeat() {
        changeSongLooping()
        mediaViewModel.createMusicNotification(position, maxPosition, looping = isLooping)
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager.cancelAll()
        unregisterReceiver(broadCastReceiver)
    }
}