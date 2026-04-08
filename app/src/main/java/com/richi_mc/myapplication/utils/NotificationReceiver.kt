package com.richi_mc.myapplication.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showNotification(
            "¡No olvides tus tickets!",
            "Recuerda escanear tus tickets de hoy para mantener tus finanzas al día."
        )
    }
}
