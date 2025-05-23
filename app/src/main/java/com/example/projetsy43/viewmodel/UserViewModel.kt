package com.example.projetsy43.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel : ViewModel() {
    private val _role = MutableStateFlow<String?>(null)
    val role: StateFlow<String?> = _role

    fun setRole(userRole: String) {
        _role.value = userRole
    }
}