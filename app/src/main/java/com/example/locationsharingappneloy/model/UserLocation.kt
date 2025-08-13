package com.example.locationsharingappneloy.model

data class UserLocation(
    val uid: String="",
    val name: String= "",
    val photoUrl: String? =null,
    val lat: Double?=null,
    val lng: Double?=null,
    val accuracy: Float? =null,
    val updateAt: Long? =null

)
