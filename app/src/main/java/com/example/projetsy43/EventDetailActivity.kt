package com.example.projetsy43
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import java.sql.Date
import androidx.compose.foundation.layout.width
import androidx.compose.ui.platform.LocalContext


class EventDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recuperer les donnees de la DB via Intent
        val title = intent.getStringExtra("title") ?: "Unknown Event"
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""
        val date = intent.getStringExtra("date") ?: "Unknown Event"
        val location = intent.getStringExtra("location") ?: "Unknown Event"
        val description = intent.getStringExtra("description") ?: "No description for the moment"

        // Affichage de l'interface utilisateur via la fonction ci dessous (en parametre, les donnees de la DB)
        setContent {
            EventDetailScreen(title = title, imageUrl = imageUrl, date = date, location = location, description = description)
        }
    }
}

//Interface utilisateur pour un evenement
@Composable
fun EventDetailScreen(title: String, imageUrl: String, date: String, location: String, description: String) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Barre du haut
            Box(
                modifier = Modifier
                    .fillMaxWidth() // occupe tt l'ecran
                    .height(85.dp) // taille
                    .background(Color.LightGray), //couleur barre
                contentAlignment = Alignment.TopStart
            ){

                //Place les icones une a droite et l'autre a gauche
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ){

                    //Icon Retour sur la barre superieur
                    val context = LocalContext.current
                    Icon(
                        painter = painterResource(id = R.drawable.ic_retour), //acces a l'icon
                        contentDescription = "return",
                        modifier = Modifier
                            .size(28.dp)
                            .offset(y = 20.dp) //ordonne pour deplacer l'icone

                            // Retour a l'ecran HomeActivity lors du click
                            .clickable {
                                val intent = Intent(context, HomeActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                context.startActivity(intent)
                            },
                        tint = Color.Black
                    )

                    //Icon favoris sans clickable pour le moment
                    Icon(
                        painter = painterResource(id = R.drawable.ic_favoris),
                        contentDescription = "favoris",
                        modifier = Modifier
                            .offset(y = 20.dp)
                            .size(28.dp)
                    )
                }

            }

            // Verfie si l'image n'est pas vide puis l'affichage via Coil
            if (imageUrl.isNotBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentScale = ContentScale.Crop // permet d'ajuster la taille de toute les images a la meme taille

                )
            }

            //Ajout d'un espace entre l'image et le texte "title"
            Spacer(modifier = Modifier.height(30.dp))

            //Recupere titre de la BD et l'affiche
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center //centre le texte
            )

            // Espace entre le titre et le reste
            Spacer(modifier = Modifier.height(20.dp))

            // Affiche icon de location et le text associe de la BD
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    painter = painterResource(id = R.drawable.ic_lugar),
                    contentDescription = "location",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(6.dp)) // espace entre l'icon et le texte
                Text(
                    text = location,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Start // texte au debut
                )
            }

            //espace entre location et date
            Spacer(modifier = Modifier.height(10.dp))

            // Affiche icon de calendrier et le text "date" associe de la BD
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "Date",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = date,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }

            //espace icon calendrier et texte description
            Spacer(modifier = Modifier.height(20.dp))

            //text de description recupere de la BD
            Text(
                text = description,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 24.dp),
                textAlign = TextAlign.Justify
            )
        }

        //Barre inferieur grise
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxWidth()
                .height(85.dp)
                .background(Color.LightGray)
        ){
            // Boutton sur la barre inferieur
            Button(
                onClick = { /*action a specifier plus tard */},
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Color.Black
                ),
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.6f)
                    .height(50.dp)
            ) {
                Text("Purchase Now", fontSize = 18.sp) // text du boutton + style
            }
        }


    }
}



