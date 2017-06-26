package com.jaybirdsport.virtualrunpartner.location

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.jcraw.locationupdates.R

class LocationNotification(val context: Context) {
    val ID: Int = 1234
    val CHANNEL_ID = "com.jcraw.location_updates_channel"

    lateinit var notification: Notification

    fun display() {
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        setAndroidOChannel()
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                .setContentTitle("Location Updates")
                .setTicker("Location Updates")
                .setContentText("Location Updates")
                .setOngoing(true)
        notification = builder.build()
        notificationManager.notify(ID, notification)
    }

    fun remove() {
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(ID)
    }

    fun setAndroidOChannel() {
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = CHANNEL_ID
            val channelName = "Location Updates"
            val channelImportance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId, channelName, channelImportance)
            channel.description = "Testing the receiving of Location Updates when Android O goes into IDLE"
            notificationManager.createNotificationChannel(channel)
        }
    }
}
