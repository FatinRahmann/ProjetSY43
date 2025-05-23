package com.example.projetsy43.model.entities

data class User(
    val uid: String = "",
    val prenom: String = "",
    val nom: String = "",
    val email: String = "",
    val role: String = ""
    //Fix fields here based on need. This dataclass will be stored in a singleton
)