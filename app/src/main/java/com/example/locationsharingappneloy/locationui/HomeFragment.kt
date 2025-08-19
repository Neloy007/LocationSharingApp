package com.example.locationsharingappneloy.locationui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.locationsharingappneloy.R

class HomeFragment : Fragment() {

    companion object {
        private const val ARG_LAT = "lat"
        private const val ARG_LNG = "lng"
        private const val ARG_NAME = "name"

        fun newInstance(lat: Double, lng: Double, name: String): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putDouble(ARG_LAT, lat)
            args.putDouble(ARG_LNG, lng)
            args.putString(ARG_NAME, name)
            fragment.arguments = args
            return fragment
        }
    }

    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lat = it.getDouble(ARG_LAT)
            lng = it.getDouble(ARG_LNG)
            name = it.getString(ARG_NAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate your home fragment layout here
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}
