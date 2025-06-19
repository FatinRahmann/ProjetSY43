package com.example.projetsy43.model.entities
/**
 * Represents a User entity in the application.
 *
 * @property uid Unique identifier of the user.
 * @property prenom User's first name.
 * @property nom User's last name.
 * @property email User's email address.
 * @property role Role of the user (e.g., admin, customer, seller).
 */
data class User(
    val uid: String = "",
    val prenom: String = "",
    val nom: String = "",
    val email: String = "",
    val role: String = ""
    //Fix fields here based on need. This dataclass will be stored in a singleton
)