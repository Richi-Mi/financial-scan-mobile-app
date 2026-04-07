package com.richi_mc.myapplication

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.richi_mc.myapplication.worker.NotificationWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class FinantialScanApp : Application() {
    override fun onCreate() {
        super.onCreate()
        setupNotificationWorker()
    }

    private fun setupNotificationWorker() {
        val workManager = WorkManager.getInstance(this)

        // Tarea periódica (cada 12 horas)
        val periodicWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            12, TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            "ExpenseReminderWork",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )

        // Tarea inmediata solo para la demo
        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>().build()
        workManager.enqueue(oneTimeWorkRequest)
    }
}