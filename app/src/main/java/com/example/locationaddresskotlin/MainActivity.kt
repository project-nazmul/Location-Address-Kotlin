package com.example.locationaddresskotlin

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var locationText:TextView
    lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    val locationRequestId = 100
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationText = findViewById(R.id.locationText)
        var getLocation = findViewById<Button>(R.id.getLocation)

        getLocation()

        getLocation.setOnClickListener {
            getLocation()
        }
    }

    private fun getLocation(){
        if(checkForLocationPermission()){
            updateLocation()
        }else{
            askLocationPermission()
        }
    }

    @SuppressLint("MissingPermission")
    fun updateLocation(){
        var locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationProviderClient.requestLocationUpdates(locationRequest,mLocationCallback,Looper.myLooper())
    }

    private var mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            var location = p0.lastLocation
            var geoCoder = Geocoder(applicationContext,Locale.getDefault())
            var addressList = geoCoder.getFromLocation(location!!.latitude, location!!.longitude,1) as ArrayList<Address>
            if(addressList.size>0){
                locationText.text = addressList[0].getAddressLine(0)
            }
        }
    }

    private fun checkForLocationPermission() : Boolean{
        return ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
    }

    private fun askLocationPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),locationRequestId)
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == locationRequestId){
            if(grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //get Address
                getLocation()
            }
        }
    }
}