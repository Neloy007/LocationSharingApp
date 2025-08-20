package com.example.locationsharingappneloy.locationui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.locationsharingappneloy.R
import com.example.locationsharingappneloy.data.viewmodel.AuthenticationViewModel
import com.example.locationsharingappneloy.data.viewmodel.FirestoreViewModel
import com.example.locationsharingappneloy.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthenticationViewModel
    private lateinit var firestoreViewModel: FirestoreViewModel
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModels
        authViewModel = ViewModelProvider(requireActivity())[AuthenticationViewModel::class.java]
        firestoreViewModel = ViewModelProvider(requireActivity())[FirestoreViewModel::class.java]

        loadUserInfo()

        // Edit Profile button click listener
        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(requireContext(), ProfileFragment::class.java)
            startActivity(intent)
        }
    }

    private fun loadUserInfo() {
        val currentUser = authViewModel.getCurrentUser()
        if (currentUser != null) {
            binding.tvUserEmail.text = currentUser.email ?: "No Email"

            // Fetch user details from Firestore
            firestoreViewModel.getUser(currentUser.uid) { user ->
                if (user != null) {
                    binding.tvUserName.text = user.displayName.ifEmpty { "Unknown User" }
                    binding.tvUserLocation.text = user.location.ifEmpty { "No location set" }
                } else {
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                }
            }

            // Load avatar (from FirebaseAuth photoUrl)
            Glide.with(this)
                .load(currentUser.photoUrl ?: R.drawable.profile)
                .placeholder(R.drawable.profile)
                .into(binding.imgAvatar)

        } else {
            binding.tvUserName.text = "Guest"
            binding.tvUserEmail.text = "Not logged in"
            binding.tvUserLocation.text = "-"
            binding.imgAvatar.setImageResource(R.drawable.profile)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
