package com.example.betabudget

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.text.intl.Locale
import androidx.core.os.LocaleListCompat
import com.example.betabudget.util.SessionManager

class MyApplication : Application() {
    companion object {
        const val TODO_CHANNEL_ID = "TODO_CHANNEL"
    }

    override fun onCreate() {
        super.onCreate()

        // 1. SET APP LANGUAGE ON STARTUP
        val sessionManager = SessionManager(this)
        val languageCode = sessionManager.fetchLanguage()
        setAppLocale(languageCode)

        Log.d("MyApplication", "Application onCreate: Creating notification channel.")
        // 2. CREATE NOTIFICATION CHANNEL
        createNotificationChannel()
    }

    /**
     * Sets the app's locale based on the saved language code.
     * "system" maps to the system default.
     */
    private fun setAppLocale(languageCode: String) {
        val locale = when (languageCode) {
            "af", "zu", "xh", "en" -> LocaleListCompat.create(Locale(languageCode))
            else -> LocaleListCompat.getEmptyLocaleList() // "system" maps to this
        }
        AppCompatDelegate.setApplicationLocales(locale)
        Log.d("MyApplication", "Setting app locale to: $languageCode")
    }

    /**
     * Creates the Notification Channel for Android 8.0 (Oreo) and higher.
     * This uses string resources to support multi-language.
     */
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Get strings from resources
            val name = applicationContext.getString(R.string.notification_channel_name)
            val descriptionText = applicationContext.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(TODO_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
            Log.i("MyApplication", "Notification channel created.")
        }
    }
}

