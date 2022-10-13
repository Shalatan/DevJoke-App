package com.shalatan.devjoke

import android.app.Application
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.shalatan.devjoke.notification.NotificationWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        notification()
    }

    private fun notification() {
        val workManager = WorkManager.getInstance(applicationContext)
        val constraints = Constraints.Builder()
            .build()

        val work = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()
        workManager.enqueue(work)
    }

}