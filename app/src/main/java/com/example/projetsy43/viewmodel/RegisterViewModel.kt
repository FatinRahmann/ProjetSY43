package com.example.projetsy43.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class RegisterViewModel: ViewModel() {
    var type = mutableStateOf("buyer")
}