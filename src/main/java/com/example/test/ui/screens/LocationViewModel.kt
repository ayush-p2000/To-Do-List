package com.example.test.ui.screens

import android.app.Application
import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

class LocationViewModel (app: Application): AndroidViewModel(app) {
    var tracking by mutableStateOf(false)
    var location:Location? by mutableStateOf<Location?>(null)
        private set
    var latitude by mutableStateOf<Double?>(null)
        private set
    var longitude by mutableStateOf<Double?>(null)
        private set

    private fun _setLocation(newLocation: Location?) {
        location = newLocation
        latitude = location?.latitude ?: null
        longitude = location?.longitude ?: null
    }

    fun updateLocation(newLocation: Location) {
        _setLocation(newLocation)
    }

    fun invalidate() {
        _setLocation(null)
    }

    fun valid():Boolean {
        return (location != null)
    }
}