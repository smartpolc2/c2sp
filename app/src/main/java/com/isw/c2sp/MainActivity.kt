package com.isw.c2sp

import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.isw.c2sp.models.USVGps


import com.isw.c2sp.ui.theme.C2UI
import com.isw.c2sp.ui.theme.C2spTheme
import com.isw.c2sp.ui.theme.LocationPermissionScreen
import com.isw.c2sp.ui.theme.MapScreen
import com.isw.c2sp.utils.checkForPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : ComponentActivity() {
    private var coroutineScope: CoroutineScope? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //C2UI(this)

            AppContent()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope?.cancel()
    }
}


@Composable
fun AppContent() {
    var markerPosition by remember { mutableStateOf(LatLng(0.0, 0.0)) }

    // Make a network request and update markerPosition accordingly
    LaunchedEffect(Unit) {
        updateMarkerPeriodically { newMarkerPosition ->
            markerPosition = newMarkerPosition
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Map(markerPosition)
    }
}

@Composable
fun Map(markerPosition: LatLng) {
    GoogleMap(
        modifier = Modifier.fillMaxSize()
        /*
        cameraPosition = CameraPosition(target = markerPosition, zoom = 12f),
        onClick = {
            // Handle map click if needed
        }

         */
    ) {
        // Place the marker on the map
        Marker(

            //position = markerPosition,
            state = MarkerState(markerPosition),
            zIndex = 1f,
            title = "usv",
            snippet = "${markerPosition.latitude},${markerPosition.longitude}"
            // You can customize the appearance of the marker if desired
        )
    }
}

//data class MarkerData(val latitude: Double, val longitude: Double)

suspend fun fetchMarkerData(): LatLng {
    // Make a network request and return the marker data
    // Use your preferred networking library or method
    val updateDelay = 5000L
    var markerData = LatLng(44.0, 26.0)

    coroutineScope {
        while(isActive){


            val result = withContext(Dispatchers.IO) {
                val url = URL("https://a5043b0f-1c90-4975-9da3-1f297cac6676.mock.pstmn.io/api/polution/getGpsPar/")
                val connection  = url.openConnection() as HttpsURLConnection

                if(connection.responseCode == 200)
                {
                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val request = Gson().fromJson(inputStreamReader, USVGps::class.java)

                    markerData = LatLng(request.Latitude / 100, request.Longitude / 100)

                    inputStreamReader.close()
                    inputSystem.close()
                }
                else
                {
                    //binding.baseCurrency.text = "Failed Connection"
                    markerData = LatLng(45.0, 26.0)
                }
            }
        }
    }
    return markerData
}

private suspend fun updateMarkerPeriodically(updateCallback: (LatLng) -> Unit) {
    // Define the delay between updates (e.g., every 10 seconds)
    val updateDelay = 70000L

    // Use a coroutine to make periodic requests
    coroutineScope {
        while (isActive) {
            val markerData = fetchMarkerData()
            updateCallback(LatLng(markerData.latitude, markerData.longitude))
            delay(updateDelay)
        }
    }
}

