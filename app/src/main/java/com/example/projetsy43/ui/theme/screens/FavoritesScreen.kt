package com.example.projetsy43.ui.theme.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projetsy43.R
import com.example.projetsy43.model.FavoritesManager
import com.example.projetsy43.model.entities.Event
import com.example.projetsy43.ui.theme.components.EventCard


// Ecran pour afficher uniquement les événements marqués comme favoris
@Composable
fun FavoritesScreen(
    allEvents: List<Event>,
    onEventClick: (Event) -> Unit,
    onGoBackClick: () -> Unit
) {
    val context: Context = LocalContext.current
    val favoriteIds = FavoritesManager.getAllFavorites(context)
    val favoriteEvents = allEvents.filter { favoriteIds.contains(it.cid) }
    Log.d("FavoritesDebug", "Total events: ${allEvents.size}, Favorites: ${favoriteIds.size}")
    favoriteIds.forEach { Log.d("FavoritesDebug", "Fav ID = $it") }
    allEvents.forEach { Log.d("FavoritesDebug", "Event = ${it.cid}") }


    Column(modifier = Modifier.padding(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp, bottom = 16.dp),

        ) {

            Icon(
                painter = painterResource(id = R.drawable.ic_retour),
                contentDescription = "return",
                modifier = Modifier
                    .align(Alignment.TopStart)  // Move icon to top left corner
                    .size(28.dp)
                    .clickable { onGoBackClick() },
                tint = Color.Black
            )

            Text(
                text = "My Favorites",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }


        // Liste verticale des événements favoris
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(favoriteEvents, key = { it.cid }) { event ->
                EventCard(event = event, onClick = { onEventClick(event) })
            }
        }

    }
}
