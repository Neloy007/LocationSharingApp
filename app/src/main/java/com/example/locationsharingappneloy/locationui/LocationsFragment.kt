package com.example.locationsharingappneloy.locationui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.locationsharingappneloy.R
import com.example.locationsharingapp.data.repo.LocationRepository
import com.example.locationsharingappneloy.adapter.UserAdapter
import com.example.locationsharingappneloy.data.location.LocationDataSource
import com.example.locationsharingappneloy.data.viewmodel.LocationViewModel
import com.example.locationsharingappneloy.databinding.FragmentLocationsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class LocationsFragment : Fragment(R.layout.fragment_locations) {

    private var _binding: FragmentLocationsBinding? = null
    private val binding get() = _binding!!

    private val adapter by lazy { UserAdapter() }

    private val viewModel: LocationViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val ds = LocationDataSource(requireContext().applicationContext)
                val repo = LocationRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
                @Suppress("UNCHECKED_CAST")
                return LocationViewModel(ds, repo) as T
            }
        }
    }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fine = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarse = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (fine || coarse) viewModel.startLocationUpdates()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLocationsBinding.bind(view)

        // Setup RecyclerView
        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsers.adapter = adapter

        // Collect users flow lifecycle-aware
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.users.collect { adapter.submitList(it) }
            }
        }

        // Start location updates
        checkLocationPermissionsAndStart()
    }

    private fun checkLocationPermissionsAndStart() {
        val finePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarsePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (finePermission == PackageManager.PERMISSION_GRANTED ||
            coarsePermission == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.startLocationUpdates()
        } else {
            requestPermissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
