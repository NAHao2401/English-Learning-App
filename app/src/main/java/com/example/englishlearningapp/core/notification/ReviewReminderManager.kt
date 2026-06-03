package com.example.englishlearningapp.core.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.englishlearningapp.MainActivity
import com.example.englishlearningapp.R

class ReviewReminderManager(context: Context) {

    private val appContext = context.applicationContext
    private val preferences = appContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun setAppForeground(isForeground: Boolean) {
        appForeground = isForeground
        if (isForeground) {
            cancelVisibleReminder()
        }
    }

    fun suppressAfterReviewActivity() {
        preferences.edit()
            .putLong(KEY_SUPPRESS_UNTIL, System.currentTimeMillis() + REVIEW_SUPPRESSION_MILLIS)
            .apply()
        cancelVisibleReminder()
    }

    fun showReviewReminder(dueCount: Int): Boolean {
        if (dueCount <= 0 || shouldSuppressReminder()) return false

        createNotificationChannel()
        if (!canPostNotifications()) return false

        val openAppIntent = PendingIntent.getActivity(
            appContext,
            0,
            Intent(appContext, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Đến giờ luyện tập")
            .setContentText("Bạn có \"$dueCount\" từ cần luyện tập")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openAppIntent)
            .setVibrate(longArrayOf(0, 300, 200, 300))
            .build()

        NotificationManagerCompat.from(appContext).notify(NOTIFICATION_ID, notification)
        preferences.edit().putLong(KEY_LAST_SHOWN_AT, System.currentTimeMillis()).apply()
        return true
    }

    fun clear() {
        preferences.edit().clear().apply()
        cancelVisibleReminder()
    }

    private fun shouldSuppressReminder(): Boolean {
        val now = System.currentTimeMillis()
        val suppressUntil = preferences.getLong(KEY_SUPPRESS_UNTIL, 0L)
        val lastShownAt = preferences.getLong(KEY_LAST_SHOWN_AT, 0L)
        return appForeground ||
            now < suppressUntil ||
            now - lastShownAt < FCM_REMINDER_COOLDOWN_MILLIS
    }

    private fun cancelVisibleReminder() {
        NotificationManagerCompat.from(appContext).cancel(NOTIFICATION_ID)
    }

    private fun canPostNotifications(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Vocabulary review",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Thông báo khi có từ vựng đến hạn luyện tập"
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 300, 200, 300)
        }
        appContext.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    companion object {
        private const val PREFERENCES_NAME = "review_reminder_preferences"
        private const val KEY_SUPPRESS_UNTIL = "suppress_until"
        private const val KEY_LAST_SHOWN_AT = "last_shown_at"
        private const val CHANNEL_ID = "vocabulary_review"
        private const val NOTIFICATION_ID = 1001
        private const val REVIEW_SUPPRESSION_MILLIS = 30 * 1_000L
        private const val FCM_REMINDER_COOLDOWN_MILLIS = 30 * 1_000L

        @Volatile
        private var appForeground = false
    }
}
