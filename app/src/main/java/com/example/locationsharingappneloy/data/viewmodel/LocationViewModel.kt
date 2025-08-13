package com.example.locationsharingappneloy.data.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationsharingapp.data.repo.LocationRepository
import com.example.locationsharingappneloy.data.location.LocationDataSource
import com.example.locationsharingappneloy.model.UserLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


class LocationViewModel(
    private val ds: LocationDataSource,
    private val repo: LocationRepository
) : ViewModel() {

    private val _myLocation = MutableStateFlow<Location?>(null)
    val myLocation: StateFlow<Location?> = _myLocation.asStateFlow()

    val users: StateFlow<List<UserLocation>> =
        repo.observeAllUsers()
            .combine(myLocation) { list, myLoc ->
                if (myLoc == null) list
                else list.map { u ->
                    if (u.lat != null && u.lng != null) {
                        val d = distanceMeters(
                            myLoc.latitude, myLoc.longitude, u.lat, u.lng
                        )
                        u.copy(name = "${u.name}  •  ${formatMeters(d)}")
                    } else u
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun startLocationUpdates() {
        viewModelScope.launch {
            ds.locationUpdates().collect { loc ->
                _myLocation.value = loc
                repo.updateMyLocation(loc.latitude, loc.longitude, loc.accuracy)
            }
        }
    }

    private fun distanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat/2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon/2).pow(2.0)
        return 2 * R * asin(min(1.0, sqrt(a)))
    }
    private fun formatMeters(m: Double) = if (m < 1000) "${m.toInt()} m" else String.format("%.2f km", m/1000)
}