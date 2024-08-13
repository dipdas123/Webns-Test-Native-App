package com.dip_int.test_webns.common

import android.content.Context
import android.content.SharedPreferences

/*** Created By Dipe K Das on: 13th Aug 2024*/

object SharedPreferencesHelper {

    private const val PREF_NAME = "app_preferences"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // Save a string value
    fun putString(context: Context, key: String, value: String) {
        val editor = getPreferences(context).edit()
        editor.putString(key, value)
        editor.apply()
    }

    // Retrieve a string value
    fun getString(context: Context, key: String, defaultValue: String? = null): String? {
        return getPreferences(context).getString(key, defaultValue)
    }

    // Save an integer value
    fun putInt(context: Context, key: String, value: Int) {
        val editor = getPreferences(context).edit()
        editor.putInt(key, value)
        editor.apply()
    }

    // Retrieve an integer value
    fun getInt(context: Context, key: String, defaultValue: Int = 0): Int {
        return getPreferences(context).getInt(key, defaultValue)
    }

    // Save a boolean value
    fun putBoolean(context: Context, key: String, value: Boolean) {
        val editor = getPreferences(context).edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    // Retrieve a boolean value
    fun getBoolean(context: Context, key: String, defaultValue: Boolean = false): Boolean {
        return getPreferences(context).getBoolean(key, defaultValue)
    }

    // Save a float value
    fun putFloat(context: Context, key: String, value: Float) {
        val editor = getPreferences(context).edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    // Retrieve a float value
    fun getFloat(context: Context, key: String, defaultValue: Float = 0f): Float {
        return getPreferences(context).getFloat(key, defaultValue)
    }

    // Save a long value
    fun putLong(context: Context, key: String, value: Long) {
        val editor = getPreferences(context).edit()
        editor.putLong(key, value)
        editor.apply()
    }

    // Retrieve a long value
    fun getLong(context: Context, key: String, defaultValue: Long = 0L): Long {
        return getPreferences(context).getLong(key, defaultValue)
    }

    // Remove a specific key
    fun remove(context: Context, key: String) {
        val editor = getPreferences(context).edit()
        editor.remove(key)
        editor.apply()
    }

    // Clear all preferences
    fun clear(context: Context) {
        val editor = getPreferences(context).edit()
        editor.clear()
        editor.apply()
    }
}
