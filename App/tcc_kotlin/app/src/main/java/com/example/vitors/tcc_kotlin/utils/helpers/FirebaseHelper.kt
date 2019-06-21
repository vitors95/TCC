package com.example.vitors.tcc_kotlin.utils.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.example.vitors.tcc_kotlin.activities.MainActivity
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.FirebaseMessagingService

class FirebaseHelper : FirebaseMessagingService() {
    private val context: Context
        get() = applicationContext


    override fun onMessageReceived(p0: RemoteMessage?) {
        val title = p0?.notification?.title
        val data = p0?.notification?.body
        buildNotification(title, data)
    }

    private fun buildNotification(title: String?, data: String?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = createNotificationBuilder(notificationManager)
        val pendingIntent = buildPendingIntent()
        updateNotificationBuilder(builder, title, data, pendingIntent)
        notificationManager.notify(0, builder.build())
    }

    private fun createNotificationBuilder(notificationManager: NotificationManager): NotificationCompat.Builder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel("TCC", "TCC", importance)
            notificationManager.createNotificationChannel(notificationChannel)
            notificationChannel.enableVibration(true)
            notificationChannel.enableLights(true)
            NotificationCompat.Builder(applicationContext, notificationChannel.id)
        } else {
            NotificationCompat.Builder(applicationContext)
        }
    }

    private fun updateNotificationBuilder(builder: NotificationCompat.Builder, title: String?, data: String?, pendingIntent: PendingIntent) {
        builder
            .setContentTitle(title)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setStyle(NotificationCompat.BigTextStyle().bigText(data))
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
    }

    private fun buildPendingIntent(): PendingIntent {
        val intent = buildIntent()
        return PendingIntent.getActivity(this,  0, intent, 0)
    }

    private fun buildIntent(): Intent {
        return startIntent()
    }

    private fun startIntent(): Intent {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return intent
    }
}