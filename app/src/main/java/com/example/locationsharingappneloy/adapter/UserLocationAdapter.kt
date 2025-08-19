package com.example.locationsharingappneloy.locationui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.locationsharingappneloy.R
import com.example.locationsharingappneloy.databinding.ItemUserLocationBinding
import com.example.locationsharingappneloy.model.UserLocation

class UserLocationAdapter(
    private var userList: List<UserLocation>,
    private val onItemClick: (UserLocation) -> Unit
) : RecyclerView.Adapter<UserLocationAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: ItemUserLocationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserLocationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        with(holder.binding) {
            tvName.text = user.name
            tvLocation.text = if (user.lat != null && user.lng != null) {
                "Lat: ${user.lat}, Lng: ${user.lng}"
            } else "Location not available"

            Glide.with(root.context)
                .load(user.photoUrl ?: R.drawable.profilex)
                .placeholder(R.drawable.profilex)
                .into(imgAvatar)

            root.setOnClickListener { onItemClick(user) }
        }
    }

    override fun getItemCount(): Int = userList.size

    fun updateData(newList: List<UserLocation>) {
        userList = newList
        notifyDataSetChanged()
    }
}
