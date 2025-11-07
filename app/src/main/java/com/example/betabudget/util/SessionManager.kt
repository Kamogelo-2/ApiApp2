package com.example.betabudget.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences("BetaBudgetPrefs", Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_ID = "user_id"

        // --- START: NEW KEYS ---
        const val ENABLE_NOTIFICATIONS = "enable_notifications"
        const val APP_LANGUAGE = "app_language"
        // --- END: NEW KEYS ---
    }


    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun saveUserId(id: Int) {
        val editor = prefs.edit()
        editor.putInt(USER_ID, id)
        editor.apply()
    }

    fun fetchUserId(): Int {
        return prefs.getInt(USER_ID, -1) // -1 indicates no user
    }
    // --- START: NEW METHODS ---
    fun saveNotificationSetting(isEnabled: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(ENABLE_NOTIFICATIONS, isEnabled)
        editor.apply()
    }

    fun areNotificationsEnabled(): Boolean {
        // Default to 'true'
        return prefs.getBoolean(ENABLE_NOTIFICATIONS, true)
    }

    fun saveLanguage(languageCode: String) {
        val editor = prefs.edit()
        editor.putString(APP_LANGUAGE, languageCode)
        editor.apply()
    }

    fun fetchLanguage(): String {
        // "system" will be our code for system default
        return prefs.getString(APP_LANGUAGE, "system") ?: "system"
    }
    // --- END: NEW METHODS ---

    fun clearData() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}