package com.shalatan.devjoke.util

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.shalatan.devjoke.R

private const val NOTIFICATION_ID = 0

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