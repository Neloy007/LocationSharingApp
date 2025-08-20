package com.example.locationsharingappneloy.locationui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.locationsharingappneloy.R
import com.example.locationsharingappneloy.data.viewmodel.FirestoreViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var firestoreViewModel: FirestoreViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val myUserId: String = firebaseAuth.currentUser?.uid ?: ""
    private var myDisplayName: String = "Me"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        firestoreViewModel = ViewModelProvider(this).get(FirestoreViewModel::class.java)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true

        // Show my current location with marker
        zoomToMyLocation()

        // Fetch all other users and show their markers
        firestoreViewModel.getAllUsers { userList ->
            for (user in userList) {
                if (user.userId == myUserId) continue
                val latLng = parseLocation(user.location)
                googleMap.addMarker(
                    MarkerOptions().position(latLng).title(user.displayName)
                )
            }
        }
    }

    private fun zoomToMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
            return
        }

        googleMap.isMyLocationEnabled = true // show blue dot

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val myLatLng = LatLng(location.latitude, location.longitude)

                // Fetch displayName from Firestore
                firestoreViewModel.getUser(myUserId) { user ->
                    myDisplayName = user?.displayName ?: "Me"

                    // Add marker with name
                    googleMap.addMarker(
                        MarkerOptions().position(myLatLng).title(myDisplayName)
                    )

                    // Move camera to my location
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15f))

                    // Update location in Firestore
                    uploadMyLocationToFirestore(location)
                }
            } else {
                Toast.makeText(this, "Unable to fetch your location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun parseLocation(location: String): LatLng {
        // Expect: "Lat: 23.7, Lng: 90.4"
        val latLngSplit = location.split(", ")
        val latitude = latLngSplit.getOrNull(0)?.substringAfter("Lat: ")?.toDoubleOrNull() ?: 0.0
        val longitude = latLngSplit.getOrNull(1)?.substringAfter("Lng: ")?.toDoubleOrNull() ?: 0.0
        return LatLng(latitude, longitude)
    }

    private fun uploadMyLocationToFirestore(location: Location) {
        val locString = "Lat: ${location.latitude}, Lng: ${location.longitude}"
        firestoreViewModel.updateUserLocation(myUserId, locString)
    }
}
