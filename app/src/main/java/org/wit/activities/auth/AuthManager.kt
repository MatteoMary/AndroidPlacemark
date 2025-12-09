package org.wit.activities.auth

import android.content.Context

object AuthManager {

    private const val PREFS_NAME = "athlete_auth_prefs"
    private const val KEY_LOGGED_IN = "logged_in"
    private const val KEY_USERNAME = "username"
    private const val KEY_PASSWORD = "password"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isLoggedIn(context: Context): Boolean =
        prefs(context).getBoolean(KEY_LOGGED_IN, false)

    fun isRegistered(context: Context): Boolean =
        prefs(context).contains(KEY_USERNAME) && prefs(context).contains(KEY_PASSWORD)

    fun register(context: Context, username: String, password: String) {
        prefs(context).edit()
            .putString(KEY_USERNAME, username)
            .putString(KEY_PASSWORD, password)
            .apply()
    }

    fun login(context: Context, username: String, password: String): Boolean {
        val storedUsername = prefs(context).getString(KEY_USERNAME, null)
        val storedPassword = prefs(context).getString(KEY_PASSWORD, null)

        val success = (username == storedUsername && password == storedPassword)
        if (success) {
            prefs(context).edit().putBoolean(KEY_LOGGED_IN, true).apply()
        }
        return success
    }

    fun logout(context: Context) {
        prefs(context).edit().putBoolean(KEY_LOGGED_IN, false).apply()
    }

    fun getUsername(context: Context): String? =
        prefs(context).getString(KEY_USERNAME, null)
}
