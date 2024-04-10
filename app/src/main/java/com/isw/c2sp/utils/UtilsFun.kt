package com.isw.c2sp.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil

fun checkForPermission(context: Context): Boolean {
    return !(ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) != PackageManager.PERMISSION_GRANTED)
}

@SuppressLint("MissingPermission")

fun getCurrentLocation(context: Context, onLocationFetched: (location: LatLng) -> Unit) {
    var loc: LatLng
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fusedLocationClient.lastLocation
        .addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                loc = LatLng(latitude,longitude)
                onLocationFetched(loc)
            }
        }
        .addOnFailureListener { exception: Exception ->
            // Handle failure to get location
            Log.d("MAP-EXCEPTION",exception.message.toString())
        }

}

fun simUsvPos(c2Pos: LatLng) :LatLng {
    //delay(10000)
    return LatLng(c2Pos.latitude + 0.007, c2Pos.longitude)
}

fun calculateDistance(latlngList: List<LatLng>): Double {
    var totalDistance = 0.0

    for (i in 0 until latlngList.size - 1) {
        totalDistance += SphericalUtil.computeDistanceBetween(latlngList[i],latlngList[i + 1])

    }

    return (totalDistance * 0.001)
}

fun formattedValue(value: Double) = String.format("%.2f",value)