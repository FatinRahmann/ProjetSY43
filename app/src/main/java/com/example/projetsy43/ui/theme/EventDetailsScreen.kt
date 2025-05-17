package com.example.projetsy43.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.projetsy43.R

// Interface utilisateur pour un evenement
@Composable
fun EventDetailScreen(
    title: String,
    imageUrl: String = "",
    date: String = "Unknown",
    location: String = "Unknown",
    description: String = "No description for the moment"
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Barre du haut
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(85.dp)
                    .background(Color.LightGray),
                contentAlignment = Alignment.TopStart
            ) {

                // Place les icones une à droite et l'autre à gauche
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {

                    // Icon Retour sur la barre superieure
                    // ⚠️ Replace navigation manually
                    Icon(
                        painter = painterResource(id = R.drawable.ic_retour),
                        contentDescription = "return",
                        modifier = Modifier
                            .size(28.dp)
                            .offset(y = 20.dp)
                            .clickable {
                                // Back navigation
                                // We'll update this later with navController.popBackStack()
                            },
                        tint = Color.Black
                    )

                    // Icon favoris sans clickable pour le moment
                    Icon(
                        painter = painterResource(id = R.drawable.ic_favoris),
                        contentDescription = "favoris",
                        modifier = Modifier
                            .offset(y = 20.dp)
                            .size(28.dp)
                            .clickable {
                                // action future
                            }
                    )
                }
            }

            // Vérifie si l'image n'est pas vide puis l'affiche via Coil
            if (imageUrl.isNotBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Récupère titre de la BD et l'affiche
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Affiche icône de localisation et le texte associé
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_lugar),
                    contentDescription = "location",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = location,
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Affiche icône calendrier + date
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "Date",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = date,
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Description
            Text(
                text = description,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 24.dp),
                textAlign = TextAlign.Justify
            )
        }

        // Barre inferieure grise
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxWidth()
                .height(85.dp)
                .background(Color.LightGray)
        ) {
            // Bouton d'achat
            Button(
                onClick = { /* action à venir */ },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Color.Black
                ),
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.6f)
                    .height(50.dp)
            ) {
                Text("Purchase Now", fontSize = 18.sp)
            }
        }
    }
}
