package com.shalatan.devjoke.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.shalatan.devjoke.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        val joke = "Joke FROM doWORK"
        sendNotification(
            channelId = applicationContext.getString(R.string.notification_channel_id),
            channelName = applicationContext.getString(R.string.notification_channel_name),
            joke = joke,
            applicationContext = applicationContext
        )
        return Result.success()
    }

}