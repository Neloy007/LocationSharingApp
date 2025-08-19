package com.example.locationsharingappneloy.data.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationsharingappneloy.data.location.LocationDataSource
import com.example.locationsharingappneloy.data.repo.LocationRepository
import com.example.locationsharingappneloy.model.UserLocation
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.*

// ViewModel for handling user locations
class LocationViewModel(
    private val dataSource: LocationDataSource,
    private val repository: LocationRepository
) : ViewModel() {

    // Holds my current location
    private val _myLocation = MutableStateFlow<Location?>(null)
    val myLocation: StateFlow<Location?> = _myLocation.asStateFlow()

    // Users list combined with distance from my location
    val users: StateFlow<List<UserLocation>> = repository.observeAllUsers()
        .combine(myLocation) { userList, myLoc ->
            if (myLoc == null) userList
            else userList.map { user ->
                if (user.lat != null && user.lng != null) {
                    val distance = calculateDistanceMeters(
                        myLoc.latitude, myLoc.longitude,
                        user.lat, user.lng
                    )
                    user.copy(name = "${user.name} • ${formatDistance(distance)}")
                } else user
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Start collecting location updates from data source
    fun startLocationUpdates() {
        viewModelScope.launch {
            dataSource.locationUpdates().collect { loc ->
                _myLocation.value = loc
                repository.updateMyLocation(loc.latitude, loc.longitude, loc.accuracy)
            }
        }
    }

    // Haversine formula to calculate distance between two coordinates
    private fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371000.0 // Earth radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        return 2 * R * asin(min(1.0, sqrt(a)))
    }

    // Format distance in meters or kilometers
    private fun formatDistance(distance: Double): String =
        if (distance < 1000) "${distance.toInt()} m" else String.format("%.2f km", distance / 1000)
}
