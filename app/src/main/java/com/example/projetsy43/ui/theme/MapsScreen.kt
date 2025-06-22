package com.example.projetsy43.ui.theme
import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.projetsy43.R
import com.example.projetsy43.model.repository.EventRepository
import com.example.projetsy43.viewmodel.HomeViewModel
import com.example.projetsy43.factory.HomeViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale


//Composable function to display the map and markers on it
@Composable
fun MapsScreen(navController: NavHostController) {

    val context = LocalContext.current
    val repository = remember { EventRepository() }
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
    val events = viewModel.allEvents

    // State variables for Google Map and event locations
    var googleMap by remember { mutableStateOf<GoogleMap?>(null) }
    var eventLocations by remember { mutableStateOf<List<Pair<String, LatLng>>>(emptyList()) }

    Column(modifier = Modifier.padding(16.dp).zIndex(0f))
    {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {

            //Back Icon
            Icon(
                painter = painterResource(id = R.drawable.ic_retour),
                contentDescription = "return",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(28.dp)
                    .clickable {
                        navController.popBackStack()
                    },
                tint = Color.Black
            )
        }

        //Récupérer les events au lancement de l'app (une fois)
        LaunchedEffect(Unit) {
            viewModel.fetchEvents()
        }

        //Si les events changent, recalculer les positions des marqueurs
        LaunchedEffect(events) {
            val locations = mutableListOf<Pair<String, LatLng>>()
            for (event in events) {
                val latLng = geocodeAddress(context, event.address)
                if (latLng != null) {
                    locations.add(event.name to latLng)
                }
            }
            eventLocations = locations
        }

        //Vue Android native dans le composable
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    onCreate(Bundle())
                    onResume()

                    getMapAsync { map ->
                        googleMap = map

                        //Activer les controles de zoom
                        map.uiSettings.isZoomControlsEnabled = true
                    }
                }
            },
            update = {

                //Si map pas encore initialisé, sortir
                val map = googleMap ?: return@AndroidView

                map.clear()
                val boundsBuilder = LatLngBounds.Builder()

                //Ajouter les marqueurs pour chaque position event
                for ((name, location) in eventLocations) {
                    map.addMarker(MarkerOptions().position(location).title(name))
                    boundsBuilder.include(location)
                }

                //Zoom sur les marqueurs pour tout afficher
                if (eventLocations.isNotEmpty()) {
                    val bounds = boundsBuilder.build()
                    val padding = 100
                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
                }
            }
        )
    }
}


//focntion pour geocoder une adresse en coordonnees LatLng
suspend fun geocodeAddress(context: Context, address: String): LatLng? {

    //execution dans un thread d'I/O pour ne pas bloquer l'UI
    return withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val results = geocoder.getFromLocationName(address, 1)
            if (!results.isNullOrEmpty()) {
                val loc = results[0]
                LatLng(loc.latitude, loc.longitude)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
