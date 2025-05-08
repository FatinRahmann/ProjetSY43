package com.example.projetsy43

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.example.projetsy43.databinding.ActivityMapsBinding
import java.util.*


//herite de AppCompatactivity, activite ecran et onmapreadycallback : interface implemente methode onmapready pour interagir avec la carte
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap //carte
    private lateinit var binding: ActivityMapsBinding //element ui lier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //binding evite appel direct
        binding = ActivityMapsBinding.inflate(layoutInflater) // lie elements du fichier activity_maps xml a classe mapsactvity
        setContentView(binding.root)

        // recupere carte (fragmenet dans le layout xml)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this) //envoi demande carte prete
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //ajout des boutons zooms
        mMap.uiSettings.isZoomControlsEnabled = true


        // recupere events via fetchevent definis dans homeactvity
        fetchEvents { events ->
            // builder limite carte
            val boundsBuilder = LatLngBounds.Builder()

            for (event in events) {
                // ajout marqueur via addresse
                geocodeAddressAndAddMarker(this, mMap, event.addresse, boundsBuilder)
            }

            // limite carte et dezoomer
            val bounds = boundsBuilder.build()
            val padding = 100 // extension carte marge
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding) //maj camera

            // angle camera (voir tt les marqueurs) animation
            mMap.animateCamera(cameraUpdate)
        }
    }

    // recupere marqueur + ajoute / conversion
    private fun geocodeAddressAndAddMarker(
        context: Context,
        map: GoogleMap,
        address: String,
        boundsBuilder: LatLngBounds.Builder //permet maj carte
    ) {
        val geocoder = Geocoder(context, Locale.getDefault()) //objet
        val locationList = geocoder.getFromLocationName(address, 1) //geocode l adresse
        if (!locationList.isNullOrEmpty()) {
            val location = locationList[0]
            val latLng = LatLng(location.latitude, location.longitude)

            // Ajout du marqueur
            map.addMarker(MarkerOptions().position(latLng).title(address))

            // limiter la carte
            boundsBuilder.include(latLng)
        }
    }
}
