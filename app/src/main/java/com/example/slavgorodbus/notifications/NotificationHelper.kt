package com.example.slavgorodbus.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.slavgorodbus.MainActivity
import com.example.slavgorodbus.R
import java.util.Locale

object NotificationHelper {
    private const val CHANNEL_ID = "bus_departure_channel"
    private const val CHANNEL_NAME = "Уведомления об отправлении"
    private const val NOTIFICATION_ID_BASE = 1000

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления о скором отправлении автобуса"
            }
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.createNotificationChannel(channel)
            Log.d("NotificationHelper", "Notification channel '$CHANNEL_ID' created/updated.")
        }
    }

    fun showDepartureNotification(
        context: Context,
        favoriteTimeId: String,
        routeInfo: String,
        departureTimeInfo: String,
        @Suppress("UNUSED_PARAMETER") destinationInfo: String,
        departurePointInfo: String
    ) {
        createNotificationChannel(context.applicationContext)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val uniqueRequestId = (NOTIFICATION_ID_BASE.toString() + favoriteTimeId).hashCode()

        val pendingIntent = PendingIntent.getActivity(
            context,
            uniqueRequestId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val smallIconResId = R.drawable.ic_stat_directions_bus
        val combinedTitleText = "$routeInfo ${departureTimeInfo.lowercase(Locale.getDefault())}"
        val subTextParts = mutableListOf<String>()
        if (departurePointInfo.isNotBlank()) {
            subTextParts.add(departurePointInfo)
        }
        subTextParts.add("Не опаздывайте!")

        val contentSubText = subTextParts.joinToString(separator = ". ")
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(smallIconResId)
            .setContentTitle(combinedTitleText)
            .setContentText(contentSubText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.w("NotificationHelper", "POST_NOTIFICATIONS permission not granted for favoriteTimeId: $favoriteTimeId. Notification will not be shown on Android 13+.")
                return
            }
        }

        notificationManager.notify(uniqueRequestId, notification)

        Log.i("NotificationHelper", "Notification shown with ID $uniqueRequestId for $favoriteTimeId. Combined Title: '$combinedTitleText', SubText: '$contentSubText'")
    }
}