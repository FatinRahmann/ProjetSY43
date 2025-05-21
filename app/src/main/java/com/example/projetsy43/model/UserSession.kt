package com.example.projetsy43.model

import androidx.compose.runtime.mutableStateListOf
import com.example.projetsy43.model.entities.Ticket
import com.example.projetsy43.model.entities.User

//Singleton for accessing current user data and keeping the shopping cart and maybe favorites
object UserSession {

    var currentUser: User? = null
    val cartItems = mutableStateListOf<Ticket>()

    //add methods as needed!
    //implement methods related to the shopping kart
    fun isLoggedIn(): Boolean = currentUser != null

}

