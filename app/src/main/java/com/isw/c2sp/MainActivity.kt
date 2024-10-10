package com.isw.c2sp

//import android.location.Location
//import android.os.Bundle
import android.util.Log
//import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
//import androidx.core.app.ActivityCompat
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.isw.c2sp.screens.C2MainScreen
import com.isw.c2sp.screens.C2Screen
import com.isw.c2sp.utils.getUsvGps
//import com.isw.c2sp.ui.theme.C2UI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import android.widget.Toast
import android.location.Location
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.isw.c2sp.screens.saveAddress

class MainActivity : ComponentActivity() {
    private var coroutineScope: CoroutineScope? = null

    //Firebase authentication
    private lateinit var auth: FirebaseAuth

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Register for Activity Result to request permissions
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted. Get the location.
            getLastLocation()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Check if the location permission is granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLastLocation()
        } else {
            // Request permission
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // Initialize Firebase Auth
        auth = Firebase.auth

        //reload()
        val email = "mpalade@gmail.com"
        val password = "c2pollution"
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){task->
                if(task.isSuccessful){
                    // signin succesfull
                    Log.i("signin", "signInWithEmail:success")
                    val user = auth.currentUser
                }
                else{
                    //signin fail
                    Log.i("signin", "signInWithEmail:failure", task.exception)
                }
            }

        setContent {
            //C2Screen(this)
            C2MainScreen(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope?.cancel()
    }

    private fun getLastLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    // Get latitude and longitude
                    val latitude = location.latitude
                    val longitude = location.longitude

                    saveAddress(this, "c2Lat", latitude.toString())
                    saveAddress(this, "c2Lon", longitude.toString())

                    // Show location in a Toast or log it
                    Toast.makeText(
                        this,
                        "Lat: $latitude, Lon: $longitude",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Unable to get location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val LOCATION_REQUEST_CODE = 1000
    }
}
