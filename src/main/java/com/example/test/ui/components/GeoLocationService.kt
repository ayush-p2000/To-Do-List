package com.example.test.ui.components

import android.location.Location
import android.location.LocationListener
import android.util.Log
import com.example.test.ui.screens.LocationViewModel

object GeoLocationService: LocationListener {
    var locationViewModel: LocationViewModel? = null

    override fun onLocationChanged(newLocation: Location) {
        locationViewModel?.updateLocation( newLocation )
        Log.i("geolocation", "Location updated")
    }

    fun updateLatestLocation(latestLocation: Location) {
        locationViewModel?.updateLocation( latestLocation )
        Log.i("geolocation", "Location set to latest")
    }
}
