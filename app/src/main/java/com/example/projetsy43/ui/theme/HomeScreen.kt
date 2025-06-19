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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.projetsy43.R
import com.example.projetsy43.factory.HomeViewModelFactory
import com.example.projetsy43.model.UserSession
import com.example.projetsy43.model.repository.EventRepository
import com.example.projetsy43.ui.theme.components.EventCard
import com.example.projetsy43.viewmodel.HomeViewModel


// Pages redirect after login : HomePage

@Composable
fun HomeScreen(navController: NavHostController) {

    val repository = remember { EventRepository() }
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
    var searchQuery = viewModel.searchQuery
    var filteredEvents = viewModel.filteredEvents

    //TODO: Here might want to add something while its charging
    LaunchedEffect(Unit) { viewModel.fetchEvents() }

    Scaffold(
        bottomBar = {
            when (UserSession.currentUser?.role) {
                "seller" -> SellerNavbar(navController)
                "buyer" -> BuyerNavbar(navController)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically)
                {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_map),
                        contentDescription = "Location",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Locations")

                }

                // Profile
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

            // Search Bar:
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery = it },
                placeholder = { Text("Events , Artist") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Search"
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            // List all events :

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredEvents) { event ->
                    EventCard(event = event) { navController.navigate("eventDetail/${event.cid}") }
                }
            }
        }
    }
}

// Separate Composable navBar for buyers and sellers

    @Composable
    fun BuyerNavbar(navController: NavHostController) {
        Box(
            modifier = Modifier
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
                    contentDescription = "Home",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_favoris),
                    contentDescription = "Favorites",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController.navigate("favorites")
                        }
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_ticket),
                    contentDescription = "My Tickets",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController.navigate("ticketslist")
                        }
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_map),
                    contentDescription = "Map",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController.navigate("maps")
                        }
                )
            }
        }
    }

    @Composable
    fun SellerNavbar(navController: NavHostController) {
        Box(
            modifier = Modifier
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
                    painter = painterResource(id = R.drawable.ic_house), // You’ll need to add this icon too
                    contentDescription = "Modify/Delete",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController.navigate("home")
                        }
                )
                Icon(
                    painter = painterResource(id = R.drawable.sell),
                    contentDescription = "Sell Ticket",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController.navigate("addevent")
                        }
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_chart), // You’ll need to add this icon
                    contentDescription = "Statistics",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController.navigate("stats")
                        }
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_map),
                    contentDescription = "Map",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController.navigate("maps")
                        }
                )
            }
        }
    }









