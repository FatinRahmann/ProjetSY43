package com.example.projetsy43.ui.theme

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
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

@Composable
fun MapsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val repository = remember { EventRepository() }
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
    val events = viewModel.allEvents

    var googleMap by remember { mutableStateOf<GoogleMap?>(null) }
    var eventLocations by remember { mutableStateOf<List<Pair<String, LatLng>>>(emptyList()) }

    // Charger les événements
    LaunchedEffect(Unit) {
        viewModel.fetchEvents()
    }

    // Géocoder les adresses une fois les events récupérés
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

    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                onCreate(Bundle())
                onResume()

                getMapAsync { map ->
                    googleMap = map
                    map.uiSettings.isZoomControlsEnabled = true
                }
            }
        },
        update = {
            val map = googleMap ?: return@AndroidView

            map.clear()
            val boundsBuilder = LatLngBounds.Builder()

            for ((name, location) in eventLocations) {
                map.addMarker(MarkerOptions().position(location).title(name))
                boundsBuilder.include(location)
            }

            if (eventLocations.isNotEmpty()) {
                val bounds = boundsBuilder.build()
                val padding = 100
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
            }
        }
    )
}

suspend fun geocodeAddress(context: Context, address: String): LatLng? {
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
