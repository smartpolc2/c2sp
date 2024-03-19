package com.isw.c2sp.ui.theme

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
@Composable
fun MapWithPolyline() {
    var polylinePoints by remember { mutableStateOf(emptyList<LatLng>()) }
    var mapView: MapView? = null
    val context = LocalContext.current

    AndroidView({ context ->
        MapView(context).apply {
            mapView = this
            onCreate(null)
            getMapAsync { googleMap ->
                googleMap.setOnMapClickListener { latLng ->
                    // Add clicked point to the polyline
                    polylinePoints = polylinePoints + latLng
                    // Draw polyline
                    googleMap.clear()
                    googleMap.addPolyline(
                        PolylineOptions().apply {
                            addAll(polylinePoints)
                        }
                    )
                    // Add marker at the clicked point
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                    )
                    // Move camera to the clicked point
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }
            }
        }
    }, modifier = Modifier.fillMaxSize())

    DisposableEffect(Unit) {
        onDispose {
            mapView?.onDestroy()
        }
    }
}