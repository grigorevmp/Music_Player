package com.grigorevmp.musicplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private var isMusicPlaying = false
    lateinit var controlMusic: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        controlMusic = findViewById(R.id.control_music)

        controlMusic.setOnClickListener {
            musicControl()
        }

    }

    private fun musicControl() {
        if (isMusicPlaying){
            notificationDestroyer()
        }
        else{
            notificationBuilder()
        }
    }

    private fun notificationBuilder(){
        toast("Music started")
        controlMusic.setImageResource(R.drawable.ic_pause)
        isMusicPlaying = true
    }

    private fun notificationDestroyer(){
        toast("Music stopped")
        controlMusic.setImageResource(R.drawable.ic_play)
        isMusicPlaying = false
    }

    private fun toast(str: String){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
    }
}