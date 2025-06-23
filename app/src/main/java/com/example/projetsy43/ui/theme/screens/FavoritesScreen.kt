package com.example.projetsy43.ui.theme.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.projetsy43.R
import com.example.projetsy43.model.FavoritesManager
import com.example.projetsy43.model.entities.Event
import com.example.projetsy43.ui.theme.components.EventCard

@OptIn(ExperimentalMaterial3Api::class)
// Ecran pour afficher uniquement les événements marqués comme favoris
@Composable
fun FavoritesScreen(
    navController: NavHostController,
    allEvents: List<Event>
) {
    val context: Context = LocalContext.current
    val favoriteIds = FavoritesManager.getAllFavorites(context)
    val favoriteEvents = allEvents.filter { favoriteIds.contains(it.cid) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Favorites",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_retour),
                        contentDescription = "return",
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .size(28.dp)
                            .clickable { navController.popBackStack() },
                        tint = Color.Black
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            items(favoriteEvents, key = { it.cid }) { event ->
                EventCard(
                    event = event,
                    onClick = {
                        // Replace with your navigation route
                        navController.navigate("eventDetail/${event.cid}")
                    }
                )
            }
        }
    }
}
