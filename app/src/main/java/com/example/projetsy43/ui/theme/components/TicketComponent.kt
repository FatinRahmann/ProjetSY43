package com.example.projetsy43.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.border

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
    Spacer(modifier = Modifier.height(24.dp))
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF001F3F))

    ){
        Column (

        ){
            AsyncImage(
                model = imageModel,
                contentDescription = event.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(horizontal = 16.dp)
                    .padding(top = 14.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Text(
                text = event.name,
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .padding(horizontal = 16.dp)
            )
            Text(
                text = event.datetime,
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(top = 9.dp)
                    .padding(horizontal = 16.dp)
                )
            Button(
                onClick = onButtonClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF001F3F),
                    contentColor = Color(0xFF3399FF)
                ),
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .height(30.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 2.dp,
                        color = Color(0xFF3399FF),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Text(text = "See Ticket")
            }

        }


    }

}
