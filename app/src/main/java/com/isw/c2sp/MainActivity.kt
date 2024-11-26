package com.isw.c2sp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.isw.c2sp.screens.C2MainScreen
import com.isw.c2sp.screens.saveAddress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    // MutableState to store the current location
    private var currentLocationState = mutableStateOf<LatLng?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(10000)
            .setMinUpdateIntervalMillis(5000)
            .build()

        // Location update callback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.forEach { location ->
                    val latitude = location.latitude
                    val longitude = location.longitude
                    currentLocationState.value = LatLng(latitude, longitude) // Update current location state
                    Toast.makeText(
                        this@MainActivity,
                        "Location updated: Lat: $latitude, Lon: $longitude",
                        Toast.LENGTH_LONG
                    ).show()
                    saveAddress(this@MainActivity, "c2Lat", latitude.toString())
                    saveAddress(this@MainActivity, "c2Lon", longitude.toString())
                }
            }
        }

        // Authenticate with Firebase
        val email = "mpalade@gmail.com"
        val password = "c2pollution"
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.i("signin", "signInWithEmail:success")
                } else {
                    Log.i("signin", "signInWithEmail:failure", task.exception)
                }
            }

        setContent {
            // Pass currentLocationState to the MainScreen composable
            //MainScreen(currentLocationState)
            C2MainScreen(this, currentLocationState)
        }
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } else {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(currentLocationState: State<LatLng?>) {
    // Manage location permissions
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    // Request permission if not granted
    LaunchedEffect(locationPermissionState) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            if (locationPermissionState.status.isGranted) {
                MyGoogleMap(currentLocation = currentLocationState.value)
            } else {
                Text("Permission not granted")
            }
        }
    }
}

@Composable
fun MyGoogleMap(currentLocation: LatLng?) {
    // Set the default camera position
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
            currentLocation ?: LatLng(44.449762, 26.041717), // Default to fallback location if null
            14f
        )
    }

    // Update camera position when the current location changes
    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            cameraPositionState.position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(it, 14f)
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        currentLocation?.let {
            Marker(state = MarkerState(it), title = "You are here")
        }
    }
}
