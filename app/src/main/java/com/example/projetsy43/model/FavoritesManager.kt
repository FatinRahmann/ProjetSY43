package com.example.projetsy43.model

import android.content.Context

object FavoritesManager {
    private const val PREFS_NAME = "favorites_prefs"

    fun isFavorite(context: Context, eventId: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(eventId, false)
    }

    fun toggleFavorite(context: Context, eventId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = prefs.getBoolean(eventId, false)
        prefs.edit().putBoolean(eventId, !current).apply()
    }

    fun getAllFavorites(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.all.filterValues { it == true }.keys
    }
}
