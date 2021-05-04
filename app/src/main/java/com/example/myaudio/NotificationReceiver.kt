package com.example.myaudio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.myaudio.ApplicationClass.Companion.ACTION_NEXT
import com.example.myaudio.ApplicationClass.Companion.ACTION_PLAY
import com.example.myaudio.ApplicationClass.Companion.ACTION_PREVIOUS

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        var actionName: String? = intent?.action!!
        var serviceIntent = Intent(context, MusicService::class.java)
        if (actionName != null) {
            when(actionName){
                ACTION_PLAY -> {
                    serviceIntent.putExtra("ActionName","playPause")
                    context?.startService(serviceIntent)
                }
                ACTION_PREVIOUS -> {
                    serviceIntent.putExtra("ActionName","previous")
                    context?.startService(serviceIntent)
                }
                ACTION_NEXT -> {
                    serviceIntent.putExtra("ActionName","next")
                    context?.startService(serviceIntent)
                }
            }
        }
    }
}