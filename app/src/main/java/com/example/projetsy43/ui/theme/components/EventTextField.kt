package com.example.projetsy43.ui.theme.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.focus.onFocusChanged

@Composable
fun EventTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onFocus: (() -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    onFocus?.invoke()
                }
            },
        keyboardOptions = KeyboardOptions.Default,
        singleLine = true
    )
}
