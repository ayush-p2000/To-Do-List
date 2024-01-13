package com.example.test

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.test.data.DatabaseInitializer
import com.example.test.data.DatabaseInitializer2
import com.example.test.ui.components.GeoLocationService
import com.example.test.ui.screens.Geolocation
import com.example.test.ui.screens.LocationViewModel
import com.example.test.ui.screens.MainFrame
import com.example.test.ui.theme.TestTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasPermission()) {
            requestFineLocationPermission()
        }

        if (!hasPermission2()) {

            requestPermission()
        }else{
            Log.d("Permission", "has storage permission")
        }



        val testDatabase = DatabaseInitializer.initialize(this)
        val keyDAO = testDatabase.keyDAO()
        val settingDatabase = DatabaseInitializer2.initialize2(this)
        val settingDAO = settingDatabase.settingDAO()



        //val homeScreenViewModel: HomeScreenViewModel = HomeScreenViewModel(keyDAO)
        window.statusBarColor = Color.Transparent.value.toInt()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        setContent {
            val locationViewModel = viewModel<LocationViewModel>()
            GeoLocationService.locationViewModel = locationViewModel
            TestTheme {

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val REQUEST_CODE_STORAGE_PERMISSION = 1001
                    val intent = Intent(this@MainActivity, Geolocation::class.java)

                    MainFrame(keyDAO,settingDAO,intent,locationViewModel)

                }
            }
        }
    }




    private val GPS_LOCATION_PERMISSION_REQUEST = 1
    private val REQUEST_CODE_STORAGE_PERMISSION = 2
    private fun requestFineLocationPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf( android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION ),
            GPS_LOCATION_PERMISSION_REQUEST
        )
    }

    private fun hasPermission(): Boolean {
        return PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(
                    applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION )
    }


    private fun requestPermission() {
        Log.d("Permission", "Requesting storage permission")
        ActivityCompat.requestPermissions(this,
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            REQUEST_CODE_STORAGE_PERMISSION
        )
    }

    private fun hasPermission2(): Boolean {
        return PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(
                    applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE )
    }


    override fun onResume() {
        super.onResume()
        val locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        @SuppressLint("MissingPermission")
        if (hasPermission()) {
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null) {
                GeoLocationService.updateLatestLocation(location)
            }
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000, 0.0f, GeoLocationService
            )
        }
    }

    override fun onPause() {
        super.onPause()
        val locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.removeUpdates(GeoLocationService)
    }

    private val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted

            } else {
                Toast.makeText(
                    this,
                    "Until you grant the permission, we cannot display the names",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


}

