package com.example.projetsy43.ui.theme

import android.content.Context
import android.location.Geocoder
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.projetsy43.R
import com.example.projetsy43.factory.HomeViewModelFactory
import com.example.projetsy43.repository.EventRepository
import com.example.projetsy43.viewmodel.HomeViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

@Composable
fun MapsScreen(navController: NavHostController) {

    val repository = remember { EventRepository() }
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(repository)
    )
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchEvents()
    }

    var events by remember { mutableStateOf(viewModel.allEvents) }

    AndroidView(factory = { ctx ->
        val mapView = View.inflate(ctx, R.layout.activity_maps, null)

        val mapFragment = (ctx as? androidx.fragment.app.FragmentActivity)
            ?.supportFragmentManager
            ?.findFragmentById(R.id.map) as? SupportMapFragment

        mapFragment?.getMapAsync { googleMap ->
            googleMap.uiSettings.isZoomControlsEnabled = true


            val boundsBuilder = LatLngBounds.Builder()
            for (event in events) {
                geocodeAddressAndAddMarker(ctx, googleMap, event.address, boundsBuilder)
            }

            val bounds = boundsBuilder.build()
            val padding = 100
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            googleMap.animateCamera(cameraUpdate)

        }

        mapView
    })
}

// Fonction utilitaire (identique à celle dans MapsActivity)
private fun geocodeAddressAndAddMarker(
    context: Context,
    map: GoogleMap,
    address: String,
    boundsBuilder: LatLngBounds.Builder
) {
    val geocoder = Geocoder(context, Locale.getDefault())
    val locationList = geocoder.getFromLocationName(address, 1)
    if (!locationList.isNullOrEmpty()) {
        val location = locationList[0]
        val latLng = LatLng(location.latitude, location.longitude)

        map.addMarker(MarkerOptions().position(latLng).title(address))
        boundsBuilder.include(latLng)
    }
}
