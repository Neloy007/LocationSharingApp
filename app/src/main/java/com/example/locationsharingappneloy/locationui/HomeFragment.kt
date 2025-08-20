package com.example.locationsharingappneloy.locationui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.locationsharingappneloy.data.viewmodel.FirestoreViewModel
import com.example.locationsharingappneloy.databinding.FragmentHomeBinding
import com.example.locationsharingappneloy.model.User
import com.example.locationsharingappneloy.model.UserLocation
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestoreViewModel: FirestoreViewModel
    private lateinit var userAdapter: UserAdapter
    private var userList = mutableListOf<UserLocation>()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestoreViewModel = ViewModelProvider(this)[FirestoreViewModel::class.java]

        // Setup RecyclerView & adapter
        userAdapter = UserAdapter(userList) { user ->
            val intent = Intent(requireContext(), MapsActivity::class.java)
            intent.putExtra("lat", user.lat)
            intent.putExtra("lng", user.lng)
            intent.putExtra("name", user.name)
            startActivity(intent)
        }

        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
        }

        // Fetch users and display
        fetchOtherUsers()

        // Search functionality
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = userList.filter {
                    it.name.contains(newText ?: "", ignoreCase = true)
                }
                userAdapter.updateData(filtered)
                return true
            }
        })
    }

    // --- Fetch users excluding the logged-in user ---
    private fun fetchOtherUsers() {
        firestoreViewModel.getAllUsers { users: List<User> ->
            val otherUsers = users.filter { it.userId != currentUserId }
            userList.clear()
            userList.addAll(otherUsers.map { u ->
                UserLocation(
                    uid = u.userId,
                    name = u.displayName.ifEmpty { "Unknown" },
                    lat = parseLat(u.location),
                    lng = parseLng(u.location),
                    photoUrl = null
                )
            })

            requireActivity().runOnUiThread {
                userAdapter.updateData(userList)
            }
        }
    }

    private fun parseLat(location: String?): Double =
        location?.substringAfter("Lat: ")?.substringBefore(",")?.toDoubleOrNull() ?: 0.0

    private fun parseLng(location: String?): Double =
        location?.substringAfter("Lng: ")?.toDoubleOrNull() ?: 0.0

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
