package com.example.locationsharingappneloy.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.locationsharingappneloy.data.location.LocationDataSource
import com.example.locationsharingappneloy.data.repo.LocationRepository

class LocationViewModelFactory(
    private val ds: LocationDataSource,
    private val repo: LocationRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LocationViewModel(ds, repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
