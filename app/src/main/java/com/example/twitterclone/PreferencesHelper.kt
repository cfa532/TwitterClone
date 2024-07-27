package com.example.twitterclone

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveEntryUrl(url: String) {
        sharedPreferences.edit().putString("entryUrl", url).apply()
    }

    fun getEntryUrl(): String? {
        return sharedPreferences.getString("entryUrl", null)
    }

    fun saveUsername(username: String) {
        sharedPreferences.edit().putString("username", username).apply()
    }

    fun getUsername(): String? {
        return sharedPreferences.getString("username", null)
    }

    fun saveName(name: String) {
        sharedPreferences.edit().putString("name", name).apply()
    }

    fun getName(): String? {
        return sharedPreferences.getString("name", null)
    }
}