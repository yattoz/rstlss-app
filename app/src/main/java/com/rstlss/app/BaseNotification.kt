package com.rstlss.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

abstract class BaseNotification(private val notificationChannelId: String,
                                private val notificationChannel : Int,
                                private val notificationId: Int,
                                private val notificationImportance: Int
                       ) {


    // ########################################
    // ########## BASE NOTIFICATION ###########
    // ########################################

    // Define the notification in android's swipe-down menu
    lateinit var notification: Notification
    private lateinit var notificationManager: NotificationManager
    protected lateinit var builder: NotificationCompat.Builder

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected fun createNotificationChannel(c: Context): String {
        val chanName = notificationChannel
        val notificationChannelImportance =
            when(notificationImportance) {
                NotificationCompat.PRIORITY_LOW -> NotificationManager.IMPORTANCE_LOW
                NotificationCompat.PRIORITY_DEFAULT -> NotificationManager.IMPORTANCE_DEFAULT
                NotificationCompat.PRIORITY_HIGH-> NotificationManager.IMPORTANCE_HIGH
                NotificationCompat.PRIORITY_MAX -> NotificationManager.IMPORTANCE_MAX
                NotificationCompat.PRIORITY_MIN -> NotificationManager.IMPORTANCE_MIN
                else -> NotificationManager.IMPORTANCE_DEFAULT
            }
        val chan = NotificationChannel(this.notificationChannelId, c.getString(chanName), notificationChannelImportance)
        chan.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(chan)
        return this.notificationChannelId
    }

    fun show()
    {
        notification = builder.build()
        notificationManager.notify(notificationId, notification)
    }

    open fun create(c: Context) {
        notificationManager = c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationIntent = Intent(c, com.rstlss.app.MainActivity::class.java)
        // The PendingIntent will launch the SAME activity
        // thanks to the launchMode specified in the Manifest : android:launchMode="singleTop"
        val pendingIntent = PendingIntent.getActivity(
            c, 0,
            notificationIntent, 0
        )
        var channelID = ""

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelID = createNotificationChannel(c)
        }
        builder = NotificationCompat.Builder(c, channelID)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(com.rstlss.app.R.drawable.ic_notif_lollipop_icon)
            builder.color = 0xf58b01 // same color as Accent. Can't use c.getColor since it's API23+
        } else {
            builder.setSmallIcon(com.rstlss.app.R.drawable.logo_roundsquare)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        }

        builder.priority = notificationImportance

        // The PendingIntent will launch the SAME activity
        // thanks to the launchMode specified in the Manifest : android:launchMode="singleTop"
        builder.setContentIntent(pendingIntent)

        builder.setColorized(true)
    }

    fun clear()
    {
        notificationManager.cancel(notificationId)
    }
}