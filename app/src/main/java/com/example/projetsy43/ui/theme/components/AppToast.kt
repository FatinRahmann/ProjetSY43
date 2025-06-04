package com.example.projetsy43.ui.theme.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

enum class ToastType {
    SUCCESS, ERROR
}

fun getToastColor(type: ToastType): Color {
    return when (type) {
        ToastType.SUCCESS -> Color(0xFF4CAF50) // Green
        ToastType.ERROR -> Color(0xFFF44336)   // Red
    }
}

@Composable
fun AppToast(
    message: String,
    visible: Boolean,
    type: ToastType = ToastType.SUCCESS,
    onDismiss: () -> Unit
) {
    LaunchedEffect(visible) {
        if (visible) {
            delay(3000)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 64.dp)
                .background(getToastColor(type)),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = message,
                color = Color.White,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
