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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import com.example.projetsy43.R
import com.example.projetsy43.factory.HomeViewModelFactory
import com.example.projetsy43.model.UserSession
import com.example.projetsy43.ui.theme.components.EventCard
import com.example.projetsy43.model.repository.EventRepository
import com.example.projetsy43.viewmodel.HomeViewModel


// Affiche ecran d'acceuil HomeScreen
@Composable
fun HomeScreen(navController: NavHostController) {
    //TODO: Annalyse the actual need for the factory and application of Hilt
    val repository = remember { EventRepository() }
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(repository)
    )

    var searchQuery = viewModel.searchQuery // contenu barre de recherche
    var allEvents = viewModel.allEvents // liste des events charges depuis Firebase
    var filteredEvents = viewModel.filteredEvents

    //TODO: Here might want to add something while its charging
    LaunchedEffect(Unit) {
        viewModel.fetchEvents()
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
                // icon location + redirection vers la page de profil
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_map),
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
                            navController.navigate("profile")
                        }
                )
            }

            // barre de recherche avec icône
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery = it },
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

            Spacer(modifier = Modifier.height(12.dp))

            // affichage des events filtrés en grille
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // lors du clique sur une carte on accede à la page detail de l'événement
                items(filteredEvents) { event ->
                    EventCard(event = event) {
                        // remplacer Intent par une route dynamique
                        navController.navigate("eventDetail/${event.cid}")
                    }
                }
            }
        }

        // bar inférieure grise avec icons
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

                when (UserSession.currentUser?.role) {
                    "seller" -> {
                        //For testing the add event
                        Icon(
                            painter = painterResource(id = R.drawable.sell),
                            contentDescription = "AddEvent",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    navController.navigate("addevent")

                                }
                        )
                    }
                    "buyer" -> {
                        // redirige vers HomeScreen (actualise)
                        Icon(
                            painter = painterResource(id = R.drawable.ic_house),
                            contentDescription = "Accueil",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                        )
                    }
                }

                // redirige vers la carte
                Icon(
                    painter = painterResource(id = R.drawable.ic_map),
                    contentDescription = "Carte",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController.navigate("maps")
                        }
                )

                // en cours ou à venir (TODO)
                Icon(
                    painter = painterResource(id = R.drawable.ic_favoris),
                    contentDescription = "Favoris",
                    modifier = Modifier.size(24.dp)
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_ticket),
                    contentDescription = "Tickets",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}


