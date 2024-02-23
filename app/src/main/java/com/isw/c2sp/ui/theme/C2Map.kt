package com.isw.c2sp.ui.theme

import android.content.Context
import android.graphics.Color.MAGENTA
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.util.copy
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PinConfig
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.gson.Gson
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberMarkerState
import com.isw.c2sp.R
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
    newNode: Boolean? = null,
    mapProperties: MapProperties = MapProperties(),
    onNewNodeClick: (newNode: Boolean?) -> Unit,
){

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(c2Pos, 15f)
    }

    var markerPosition by remember {  mutableStateOf (LatLng(usvPos.latitude, usvPos.longitude)) } // Initial position
    var markerData by remember {  mutableStateOf (LatLng(usvPos.latitude, usvPos.longitude)) } // Initial position
    var isUSVPresent by remember { mutableStateOf(true) }

    //usv planned path
    val usvPath = remember {
        mutableStateListOf(c2Pos)
        //mutableStateListOf(LatLng)
    }

    LaunchedEffect(key1 = Unit) {
        while (true) {
            val result = withContext(Dispatchers.IO) {
                val url = URL("https://a5043b0f-1c90-4975-9da3-1f297cac6676.mock.pstmn.io/api/polution/getGpsPar/")
                val connection  = url.openConnection() as HttpsURLConnection

                if(connection.responseCode == 200)
                {
                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val request = Gson().fromJson(inputStreamReader, USVGps::class.java)

                    markerData = LatLng(request.Latitude / 100, request.Longitude / 100)
                    markerData = generateNewPosition(markerData)
                    isUSVPresent = true

                    inputStreamReader.close()
                    inputSystem.close()
                }
                else
                {
                    //binding.baseCurrency.text = "Failed Connection"
                    //markerData = LatLng(0.0, 0.0)
                    isUSVPresent = false
                }
            }

            delay(5*1000) // Change position every second
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            onMapClick = {
                /*
                if (usvPath.size == 1){
                    usvPath.clear()
                }

                 */
                usvPath.add(it)
            }
        ) {

            //markers
            c2Maker(c2Pos = c2Pos)
            usvMarker(usvPos = markerData)

            //usv planned path
            if (usvPath.size > 1){
                usvPath.toList().forEach {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Location",
                        snippet = "Marker in current location",
                        //icon = BitmapDescriptorFactory.fromResource(R.mipmap.poi)
                    )
                }
                if (newNode == true){
                    Polyline(points = usvPath, color = Color.Red)
                }
            }

        }

        Column(){
            //remote controll UI
            rcUI()
            //pollution panel UI
            pollutionUI()

            if (usvPath.size > 1){
                Button(
                    onClick = {
                        onNewNodeClick(false)
                        usvPath.clear()
                        //usvPath.add(usvPos)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(text = "Clear", color = Color.White)
                }

                Spacer(modifier = Modifier.padding(4.dp))
                Button(onClick = {
                    onNewNodeClick(true)

                }){
                    Text(text = "Draw")
                }
            }
        }
    }
}

@Composable
fun drawPath(usvPath: List<LatLng>) {
    if (usvPath.size > 1){
        usvPath.toList().forEach {
            Marker(
                state = MarkerState(position = it),
                title = "Location",
                snippet = "Marker in current location",
                //icon = BitmapDescriptorFactory.fromResource(R.mipmap.poi)
            )
        }
        Polyline(points = usvPath, color = Color.Red)
    }

}

fun generateNewPosition(currentPosition: LatLng): LatLng {
    // Replace this with your logic to generate a new marker position
    // For example, you can randomly generate a new position
    return LatLng(currentPosition.latitude + getRandomOffset(), currentPosition.longitude + getRandomOffset())
}

fun getRandomOffset(): Double {
    // Generate a small random offset for demo purposes
    return (Math.random() - 0.5) / 50.0
}

@Composable
fun c2Maker(c2Pos: LatLng){
    Marker(
        state = MarkerState(c2Pos),
        title = "C2",
        snippet = "(${c2Pos.latitude},${c2Pos.longitude})",
        icon = BitmapDescriptorFactory.fromResource(R.mipmap.c2)
    )
}

@Composable
fun usvMarker(usvPos: LatLng){
    Marker(
        state = MarkerState(usvPos),
        title = "USV",
        snippet = "(${usvPos.latitude},${usvPos.longitude})",
        icon = BitmapDescriptorFactory.fromResource(R.mipmap.usv)
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

