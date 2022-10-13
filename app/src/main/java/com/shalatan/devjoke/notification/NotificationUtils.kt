package com.shalatan.devjoke.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.shalatan.devjoke.R
import dagger.hilt.android.qualifiers.ApplicationContext

private const val NOTIFICATION_ID = 0

fun sendNotification(
    channelId: String,
    channelName: String,
    joke:String,
    @ApplicationContext applicationContext: Context
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.description = "YEAH SHAYAD CHANNEL KA NAAM HEI"

        val notificationManager = applicationContext.getSystemService(
            NotificationManager::class.java
        )
        notificationManager.createNotificationChannel(notificationChannel)
        notificationManager.sendNotification(joke, applicationContext)
    }
}

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )
        .setSmallIcon(R.drawable.dev_joke_logo)
        .setContentTitle("DevJoke App")
        .setContentText(messageBody)
    notify(NOTIFICATION_ID, builder.build())
}