package com.example.locationsharingappneloy.locationui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.locationsharingappneloy.R
import com.example.locationsharingappneloy.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // Mock user data (replace with actual user info from Firebase or DB)
    private val userName = "Himadro"
    private val userEmail = "himadro@example.com"
    private val userPhotoUrl: String? = null // Replace with actual URL

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set user info
        binding.tvUserName.text = userName
        binding.tvUserEmail.text = userEmail

        // Load avatar image with Glide
        Glide.with(this)
            .load(userPhotoUrl ?: R.drawable.profilex)
            .placeholder(R.drawable.profilex)
            .into(binding.imgAvatar)

        // Edit Profile button click listener
        binding.btnEditProfile.setOnClickListener {
            // Handle edit profile action
            // e.g., navigate to EditProfileFragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
