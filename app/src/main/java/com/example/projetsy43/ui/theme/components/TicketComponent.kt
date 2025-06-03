package com.example.projetsy43.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projetsy43.R
import coil.compose.AsyncImage
import com.example.projetsy43.model.entities.Event

@Composable
fun TicketComponent(
    event: Event,
    onButtonClick: () -> Unit  // pass your navigation or other action here
) {
    //Get image and differenciate between local or URL
    val context = LocalContext.current
    val drawableId = context.resources.getIdentifier(event.cover_image, "drawable", context.packageName)
    val imageModel = when {
        event.cover_image.startsWith("http") -> event.cover_image
        drawableId != 0 -> drawableId
        else -> R.drawable.defaultroute
    }

    // Use Modifier.fillMaxWidth() and set height to almost full screen height (e.g., 300-350.dp)
    Box(
        modifier = Modifier
            .height(300.dp)
            .width(360.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))

    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar - about 1/6 of height, gray background, event name centered
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = event.name,
                    color = Color.Black,
                    fontSize = 20.sp,
                )
            }

            // Image - takes about 3/6 (half) of the height
            AsyncImage(
                model = imageModel,
                contentDescription = event.name,
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )

            // Bottom bar - about 2/6 of height, gray background, button centered
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onButtonClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(width = 120.dp, height = 40.dp)
                ) {
                    Text(text = "See ticket")
                }
            }
        }
    }
}
