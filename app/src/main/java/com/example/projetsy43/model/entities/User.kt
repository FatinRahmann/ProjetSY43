package com.example.projetsy43.model

data class User(
    val uid: String = "",
    val name: String = "",
    val surname: String = "",
    val username: String = "",
    val role: String = ""
    //Fix fields here based on need. This dataclass will be stored in a singleton
)