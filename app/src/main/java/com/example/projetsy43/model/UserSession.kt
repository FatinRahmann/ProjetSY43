package com.example.projetsy43.model

//Singleton for accessing current user data
object UserSession {

    var currentUser: User? = null

    //add methods as needed!

    fun isLoggedIn(): Boolean = currentUser != null

}

