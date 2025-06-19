package com.example.projetsy43.ui.theme.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.projetsy43.model.FavoritesManager
import com.example.projetsy43.model.UserSession
import com.example.projetsy43.model.entities.Event

// Carte individuelle dans le HomeScreen pour chaque event
@Composable
fun EventCard(event: Event, onClick: () -> Unit) {

    val context = LocalContext.current
    var isFavorite by remember { mutableStateOf(FavoritesManager.isFavorite(context, event.cid)) }



    // definit affichage des cartes
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .clickable { onClick() }, // appel de l'action quand on clique
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {

            Box {
                // recupere image de la BD
                Image(
                    painter = rememberAsyncImagePainter(event.cover_image),
                    contentDescription = event.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )

                // icone coeur (favoris) *only visible to buyer
                if (UserSession.currentUser?.role == "buyer"){
                    IconButton(
                        onClick = {
                            FavoritesManager.toggleFavorite(context, event.cid)
                            isFavorite = !isFavorite
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Toggle Favorite",
                            tint = Color.Red

                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray) // couleur fond
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    // recupere titre et lieu de l'evenement
                    Text(
                        text = event.name,
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = event.address,
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = event.attraction,
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        maxLines = 1

                    )
                }
            }
        }
    }
}
