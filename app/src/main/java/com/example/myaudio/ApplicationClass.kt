package com.example.myaudio

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class ApplicationClass: Application() {

    companion object {
        var CHANNEL_ID_1: String? = "channel1"
        var CHANNEL_ID_2: String? = "channel2"
        var ACTION_PREVIOUS: String? = "actionprevious"
        var ACTION_NEXT: String? = "actionnext"
        var ACTION_PLAY : String? = "actionplay"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            var channel1 = NotificationChannel(CHANNEL_ID_1,"Channel(1)",NotificationManager.IMPORTANCE_HIGH)
            channel1.description = "Channel 1 desc..."

            var channel2 = NotificationChannel(CHANNEL_ID_2,"Channel(2)",NotificationManager.IMPORTANCE_HIGH)
            channel2.description = "Channel 2 desc..."


            val notificationManager =
                getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(channel1)
            notificationManager.createNotificationChannel(channel2)

        }
    }
}