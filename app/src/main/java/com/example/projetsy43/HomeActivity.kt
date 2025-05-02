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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import coil.compose.rememberAsyncImagePainter
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.font.FontWeight

//Affiche ecran d'acceuil HomeScreen
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen()
        }
    }
}

//Un evenement avec ses donnees
data class Event(val title: String = "", val imageUrl: String = "", val date: String = "", val location: String = "", val description: String = "")

//Permet de recuperer la liste des event depuis la BD
fun fetchEvents(onResult: (List<Event>) -> Unit) {
    val dbRef = FirebaseDatabase.getInstance("https://test-7b21e-default-rtdb.europe-west1.firebasedatabase.app").getReference("events")
    dbRef.get().addOnSuccessListener { snapshot ->
        val events = snapshot.children.mapNotNull { it.getValue(Event::class.java) }
        onResult(events) // transmet la liste
    }
}

//Carte individuelle dans le HomeScreen pour chaque event
@Composable
fun EventCard(event: Event, onClick: () -> Unit) {

    //definit affichage des cartes
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            //recupere image de la BD
            Image(
                painter = rememberAsyncImagePainter(event.imageUrl),
                contentDescription = event.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray) // couleur fond
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    //recupere titre et lieu de l'evenement
                    Text(
                        text = event.title,
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.location,
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        maxLines = 1
                    )
                }
            }
        }
    }
}


@Composable
fun HomeScreen() {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") } // contenu barre de recherche
    var allEvents by remember { mutableStateOf<List<Event>>(emptyList()) } // liste des events charges depuis Firebase

    //chargement des donne recuperer via fetchEvents
    LaunchedEffect(Unit) {
        fetchEvents { allEvents = it }
    }

    //filtrage recherche
    val filteredEvents = allEvents.filter {
        it.title.contains(searchQuery, ignoreCase = true)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // icon location et profil avec redirection vers page ProfileActivity
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lugar),
                        contentDescription = "Location",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Locations")
                }
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            val intent = Intent(context, ProfileActivity::class.java)
                            context.startActivity(intent)
                        }
                )
            }

            //barre de recherche + icon
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Events, Artist") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Search"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // ajout espace entre barre recherche et photo
            Spacer(modifier = Modifier.height(12.dp))

            // events filtrer en grille
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // lors du clique sur une carte on accede a la page EventDetailActivity avec toute les infos
                items(filteredEvents) { event ->
                    EventCard(event = event) {
                        val intent = Intent(context, EventDetailActivity::class.java)
                        intent.putExtra("title", event.title)
                        intent.putExtra("imageUrl", event.imageUrl)
                        intent.putExtra("date", event.date)
                        intent.putExtra("location", event.location)
                        intent.putExtra("description", event.description)
                        context.startActivity(intent)
                    }
                }
            }
        }

        // bar inferieur grise avec icons
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.LightGray)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_house),
                    contentDescription = "Accueil",
                    modifier = Modifier
                        .size(24.dp)
                        //redirige vers la page Homescreen
                        .clickable {
                            val intent = Intent(context, HomeActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            context.startActivity(intent)
                        }
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_map),
                    contentDescription = "Favoris",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {

                        }
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_favoris),
                    contentDescription = "Recherche",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                        }
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_ticket),
                    contentDescription = "Profil",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {

                        }
                )
            }
        }
    }
}








