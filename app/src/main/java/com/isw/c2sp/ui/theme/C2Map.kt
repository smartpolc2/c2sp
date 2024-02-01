package com.isw.c2sp.ui.theme

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.gson.Gson
import com.google.maps.android.compose.rememberMarkerState
import com.isw.c2sp.models.Pollution
import com.isw.c2sp.models.USVGps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

@Composable
fun C2MapUI(
    context: Context,
    c2Pos: LatLng,
    usvPos: LatLng,
    mapProperties: MapProperties = MapProperties()
){

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(c2Pos, 15f)
    }

    var markerPosition by remember {mutableStateOf(LatLng(0.0,0.0))}

    LaunchedEffect(Unit){
        updateMarkerPeriodically { newMarkerPosition ->
            markerPosition = newMarkerPosition
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
        ) {
            /*
            //markers
            c2Maker(c2Pos = c2Pos)
            usvMarker(usvPos = markerPosition)

             */

            simpleMaker(markerPosition)
        }

        Column(){
            //remote controll UI
            rcUI()
            //pollution panel UI
            pollutionUI()
        }
    }
}

@Composable
fun c2Maker(c2Pos: LatLng){
    Marker(
        state = MarkerState(c2Pos),
        title = "C2",
        snippet = "(${c2Pos.latitude},${c2Pos.longitude})"
    )
}

@Composable
fun usvMarker(usvPos: LatLng){
    val usvPos2 = LatLng(usvPos.latitude*1.0001, usvPos.longitude*1.0001)
    Marker(
        state = MarkerState(usvPos2),
        title = "USV",
        snippet = "(${usvPos2.latitude},${usvPos2.longitude})"
    )
}

@Composable
fun rcUI(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ){
        Button(onClick = {

        }){
            Text("Forward")
        }
        Row() {
            Button(onClick = {

            }){
                Text("Left")
            }
            Button(onClick = {

            }){
                Text("Right")
            }
        }
        Button(onClick = {

        }){
            Text("Backward")
        }
    }
}

@Composable
fun pollutionUI(){

    val viewModel: pollutionVM = viewModel()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ){
        Button(onClick = {
            viewModel.onButtonClick()
        }){
            Text("Pollution tmp= ${viewModel.temperature} pH= ${viewModel.pH} ORP=${viewModel.orp}")
        }
    }
}

class pollutionVM : androidx.lifecycle.ViewModel(){
    var pressure: Double by androidx.compose.runtime.mutableDoubleStateOf(0.0)
        private set
    var temperature: Double by androidx.compose.runtime.mutableDoubleStateOf(0.0)
        private set
    var pH: Double by androidx.compose.runtime.mutableDoubleStateOf(0.0)
        private set
    var orp: Double by androidx.compose.runtime.mutableDoubleStateOf(0.0)
        private set

    fun onButtonClick() {
        // Use a coroutine to perform the web service call
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {

                val url =
                    URL("https://a5043b0f-1c90-4975-9da3-1f297cac6676.mock.pstmn.io/api/polution/getPolPar/")
                val connection = url.openConnection() as HttpsURLConnection

                if (connection.responseCode == 200) {
                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val request = Gson().fromJson(inputStreamReader, Pollution::class.java)

                    handleUSVPollutionResult(request)

                    inputStreamReader.close()
                    inputSystem.close()
                } else {
                    //binding.baseCurrency.text = "Failed Connection"
                }
            }

            // Update the state based on the web service result
            //handleWebServiceResult(result)
        }
    }

    private fun handleUSVPollutionResult(pollution: Pollution) {
        pressure = pollution.Pressmbar.toDouble()
        temperature = pollution.TempCx100.toDouble() / 100
        pH = pollution.pHx100.toDouble() / 100
        orp = pollution.ORPmVx10.toDouble() / 10

    }
}

private suspend fun updateMarkerPeriodically(updateCallback: (LatLng) -> Unit) {
    val updateDelay = 5000L

    coroutineScope {
        while(isActive){
            var markerData = LatLng(0.0, 0.0)

            val result = withContext(Dispatchers.IO) {
                val url = URL("https://a5043b0f-1c90-4975-9da3-1f297cac6676.mock.pstmn.io/api/polution/getGpsPar/")
                val connection  = url.openConnection() as HttpsURLConnection

                if(connection.responseCode == 200)
                {
                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val request = Gson().fromJson(inputStreamReader, USVGps::class.java)

                    markerData = LatLng(request.Latitude, request.Longitude)

                    inputStreamReader.close()
                    inputSystem.close()
                }
                else
                {
                    //binding.baseCurrency.text = "Failed Connection"
                    markerData = LatLng(0.0, 0.0)
                }
            }

            // update callback
            updateCallback(markerData)

            delay(updateDelay)

        }
    }
}

@Composable
fun simpleMaker(markerPosition: LatLng) {

}