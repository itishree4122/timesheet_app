package com.example.myapplication.backend_api

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesHelper {
    private const val PREF_NAME = "app_prefs"
    private const val ACCESS_TOKEN_KEY = "access_token"
    private const val REFRESH_TOKEN_KEY = "refresh_token"
    private const val IS_ADMIN_KEY = "is_admin"
    private const val KEY_SUPERVISOR_NAME = "supervisor_name"
    private const val USERNAME_KEY = "username"

    // Save the supervisor name to SharedPreferences
    fun saveSupervisorName(context: Context, supervisorName: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_SUPERVISOR_NAME, supervisorName)
        editor.apply() // Save the data asynchronously
    }

    // Get the supervisor name from SharedPreferences
    fun getSupervisorName(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_SUPERVISOR_NAME, null)
    }

    // Save access token
    fun saveAccessToken(context: Context, accessToken: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(ACCESS_TOKEN_KEY, accessToken).apply()
    }

    // Save refresh token
    fun saveRefreshToken(context: Context, refreshToken: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(REFRESH_TOKEN_KEY, refreshToken).apply()
    }

    // Retrieve access token
    fun getAccessToken(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(ACCESS_TOKEN_KEY, null)
    }

    // Retrieve refresh token
    fun getRefreshToken(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(REFRESH_TOKEN_KEY, null)
    }

    // Clear tokens
    // Clear all relevant tokens and additional keys
    fun clearTokens(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .remove(ACCESS_TOKEN_KEY)
            .remove(REFRESH_TOKEN_KEY)
            .remove(IS_ADMIN_KEY)  // Clear the isAdmin value
            .remove(KEY_SUPERVISOR_NAME)  // Clear the supervisor name
            .apply()  // Apply changes asynchronously
    }

    fun saveIsAdmin(context: Context, isAdmin: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(IS_ADMIN_KEY, isAdmin).apply()
    }

    fun getIsAdmin(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(IS_ADMIN_KEY, false)
    }

    // Save the username to SharedPreferences
    fun saveUsername(context: Context, username: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(USERNAME_KEY, username)
        editor.apply() // Save the data asynchronously
    }

    // Get the username from SharedPreferences
    fun getUsername(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(USERNAME_KEY, null)
    }


}
