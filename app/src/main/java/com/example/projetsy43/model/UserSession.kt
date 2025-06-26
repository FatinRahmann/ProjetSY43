package com.example.projetsy43.model

import androidx.compose.runtime.mutableStateListOf
import com.example.projetsy43.model.entities.Ticket
import com.example.projetsy43.model.entities.User

/**
 * Singleton object to manage the current user session throughout the app.
 *
 * This includes:
 * - Storing the currently logged-in user data.
 *
 *  @property currentUser the currently logged-in user or null if no user is logged in.
 */
object UserSession {

    var currentUser: User? = null

    fun isLoggedIn(): Boolean = currentUser != null

}

