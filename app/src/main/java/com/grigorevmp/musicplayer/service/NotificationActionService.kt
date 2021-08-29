package com.grigorevmp.musicplayer.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationActionService : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.sendBroadcast(
            Intent("Songs action")
                .putExtra(
                    "action_name",
                    intent?.action)
        )
    }
}