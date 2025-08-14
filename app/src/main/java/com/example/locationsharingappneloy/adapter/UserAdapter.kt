package com.example.locationsharingappneloy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.locationsharingappneloy.databinding.ItemUserLocationBinding
import com.example.locationsharingappneloy.model.UserLocation

import java.text.SimpleDateFormat
import java.util.*

class UserAdapter : ListAdapter<UserLocation, UserAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<UserLocation>() {
        override fun areItemsTheSame(oldItem: UserLocation, newItem: UserLocation) = oldItem.uid == newItem.uid
        override fun areContentsTheSame(oldItem: UserLocation, newItem: UserLocation) = oldItem == newItem
    }

    inner class VH(val b: ItemUserLocationBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemUserLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        with(holder.b) {
            txtName.text = item.name
            val meta = when {
                item.lat != null && item.lng != null && item.updateAt != null ->
                    "Last seen: ${fmtTime(item.updateAt)} • (${item.lat}, ${item.lng})"
                else -> "No location yet"
            }
            txtMeta.text = meta
            Glide.with(imgAvatar).load(item.photoUrl).circleCrop().into(imgAvatar)
        }
    }

    private fun fmtTime(t: Long): String {
        return SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(t))
    }
}