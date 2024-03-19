package com.isw.c2sp.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline

@Composable
fun MapWithPolylines(){
    var clickedPoints by remember { mutableStateOf(listOf<LatLng>()) }
    var polyline by remember { mutableStateOf(emptyList<LatLng>()) }

    //var polylinePoints by remember { mutableStateOf(emptyList<LatLng>()) }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            onMapClick = { clickPoint ->
                clickedPoints = clickedPoints.toMutableList().apply { add(clickPoint) }
                polyline = clickedPoints
            }
        ) {
            // Other map elements like markers etc.
            DrawPolyline(polyline)
        }

    }
}

@Composable
fun DrawPolyline(polyline: List<LatLng>) {
    if (polyline.isNotEmpty())  {
        Polyline(
            points = polyline,
            color = Color.Red, // Customize color
            width = 5f, // Adjust width
            clickable = false // Disable clicks on the polyline itself
        )
        polyline.forEach(){
            Marker(
                state = MarkerState(position = it),
                title = "Location",
                snippet = "Marker in current location",
            )
        }
    }
}
