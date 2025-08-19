package com.example.locationsharingappneloy.data.repo

import com.example.locationsharingappneloy.model.UserLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class LocationRepository {

    private val _users = MutableStateFlow<List<UserLocation>>(emptyList())
    fun observeAllUsers(): Flow<List<UserLocation>> = _users

    fun updateMyLocation(lat: Double, lng: Double, accuracy: Float?) {
        val updated = _users.value.toMutableList()
        val me = UserLocation(
            uid = "me",
            name = "Me",
            lat = lat,
            lng = lng,
            accuracy = accuracy,
            updateAt = System.currentTimeMillis()
        )
        updated.removeAll { it.uid == "me" }
        updated.add(me)
        _users.value = updated
    }

    fun addUser(user: UserLocation) {
        _users.value = _users.value + user
    }
}
