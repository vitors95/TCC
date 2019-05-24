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
import io.reactivex.disposables.Disposable

class FirebaseHelper : FirebaseMessagingService() {
    private val context: Context
        get() = applicationContext
    private var disposable: Disposable? = null


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
//        return if (PreferencesHelper.isLoggedIn) {
//            notificationIntent(localEvent)
//        } else {
//            welcomeIntent()
//        }
        return welcomeIntent()
    }

//    private fun notificationIntent(localEvent: LocalEvent?): Intent {
//        val intent = if (localEvent?.event?.eventId == EventCode.ZONE_TRIGGERED.code) Intent(context, EventDetailsActivity::class.java) else Intent(context, DashboardActivity::class.java)
//        intent.putExtra(IntentConstants.LOCAL_EVENT, localEvent)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//        return intent
//    }

    private fun welcomeIntent(): Intent {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return intent
    }
}