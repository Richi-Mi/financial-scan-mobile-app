package com.richi_mc.myapplication

import android.app.Application
import com.richi_mc.myapplication.utils.NotificationHelper
import com.richi_mc.myapplication.utils.NotificationScheduler
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FinantialScanApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Crear canal de notificaciones
        NotificationHelper(this).createNotificationChannel()
        
        // Programar recordatorio diario
        NotificationScheduler(this).scheduleDailyReminder()
    }
}
