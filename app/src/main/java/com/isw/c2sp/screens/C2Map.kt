package com.isw.c2sp.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.isw.c2sp.R
import com.isw.c2sp.models.Pollution
import com.isw.c2sp.models.USVGps
import com.isw.c2sp.models.USVNode
import com.isw.c2sp.utils.getUsvData
import com.isw.c2sp.utils.getUsvGps
import com.isw.c2sp.utils.getUsvPollution
import com.isw.c2sp.utils.loadUSVPath
import com.isw.c2sp.utils.saveUSVPath
import com.isw.c2sp.utils.saveUsvData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

@Composable
fun c2MapUI(
    context: Context,
    c2Pos: LatLng,
    usvPos: LatLng,
    mapProperties: MapProperties = MapProperties()
){

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(c2Pos, 15f)
    }

    var markerData by remember {  mutableStateOf (LatLng(usvPos.latitude, usvPos.longitude)) } // Initial position
    var isUSVPresent by remember { mutableStateOf(true) }

    //usv planned path
    val usvPath = remember {
        mutableStateListOf(c2Pos)
        //mutableStateListOf(LatLng)
    }

    var clickedPoints by remember { mutableStateOf(listOf<LatLng>()) }
    var polyline by remember { mutableStateOf(emptyList<LatLng>()) }

    val pathViewModel: PathOpVM = viewModel()

    var c2OpMode by remember { mutableStateOf("") }
    var isContinuousMonitoring by remember { mutableStateOf(false) }

    //detect USV presence
    LaunchedEffect(key1 = Unit) {
        while (true) {
            val result = withContext(Dispatchers.IO) {
                val url =
                    URL("https://a5043b0f-1c90-4975-9da3-1f297cac6676.mock.pstmn.io/api/polution/getGpsPar/")
                val connection  = url.openConnection() as HttpsURLConnection
                //val connection  = url.openConnection() as HttpURLConnection

                if(connection.responseCode == 200)
                {
                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val request = Gson().fromJson(inputStreamReader, USVGps::class.java)

                    markerData = LatLng(request.Latitude / 100, request.Longitude / 100)
                    markerData = generateNewPosition(markerData)
                    pathViewModel.onNewPosition(receivedPos = markerData)
                    isUSVPresent = true

                    if (isUSVPresent){
                        if (isContinuousMonitoring){
                            //continuous monitoring enabled
                            getUsvData()
                        }
                    }

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

    //val layer = KmlLayer(map, R.raw.geojson_file, context)

    //C2 map
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            onMapClick = {
                clickPoint ->
                    usvPath.add(clickPoint)

                clickedPoints = clickedPoints.toMutableList().apply { add(clickPoint) }
                polyline = clickedPoints

                pathViewModel.onMapClick(clickPoint)
                c2OpMode = "planning"
            }
        ) {
            //markers
            c2Maker(c2Pos = c2Pos)
            usvMarker(usvPos = markerData)

            // Planned path
            Log.i("GoogleMap Recompose","${polyline.size}")
            //DrawPolyline(polyline)
        }

        Column(){
            c2MainMenu(context, "", false)

            //operations with path
            if (usvPath.size > 1){
                Button(
                    onClick = {
                        usvPath.clear()

                        clickedPoints = clickedPoints.toMutableList().apply { clear() }
                        polyline = clickedPoints

                        pathViewModel.onClearButton()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(text = "Clear", color = Color.White)
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
fun c2MainMenu(context: Context, c2OPMode: String, contMonit: Boolean){

    var locc2OpMode by remember { mutableStateOf(c2OPMode) }
    var loccontMonit by remember {
        mutableStateOf(contMonit)
    }

    Column(){
        // Planning & Monitoring buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp)
        ){
            val dButton = 25.dp
            FloatingActionButton(onClick = { locc2OpMode = "planning" },
                modifier = Modifier
                    .size(dButton, dButton)) {
                androidx.compose.material3.Icon(Icons.Default.Build, contentDescription = "Planning")
            }
            FloatingActionButton(onClick = { locc2OpMode = "monitoring" },
                modifier = Modifier
                    .size(dButton, dButton)) {
                androidx.compose.material3.Icon(Icons.Default.Place, contentDescription = "Monitoring")
            }

            //Video
            FloatingActionButton(onClick = { locc2OpMode = "video" },
                modifier = Modifier
                    .size(dButton, dButton)) {
                androidx.compose.material3.Icon(Icons.Default.PlayArrow, contentDescription = "Video")
            }

            Spacer(modifier = Modifier.size(50.dp))

            Log.i("c2OpMode",   c2OPMode)
            //display available operations depending on c2 modes
            if (locc2OpMode == "planning"){
                showPlanningMenu(context)
            }

            if (locc2OpMode == "monitoring"){
                showMonitoringMenu(context)
            }

            if (locc2OpMode == "video"){
                showVideoMenu(context)
            }
        }
    }
}

@Composable
fun displayUsvPlannedPath(usvPath: List<LatLng>){
    Polyline(points = usvPath, color = Color.Red)
}

@Composable
fun pollutionUI(){

    val viewModel: PollutionVM = viewModel()

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
        Row(){
            //continuous monitoring
            val checkedState = remember { mutableStateOf(false) }
            Switch(
                checked = checkedState.value,
                onCheckedChange = {
                    checkedState.value = it
                    //continuos DAQ from usv
                    if(it){
                        viewModel.onContinuousMonitoring()
                    }

                }
            )
            Text("continuous DAQ")
        }

    }
}

class PathOpVM: androidx.lifecycle.ViewModel(){
    /*
    var usvPath = mutableListOf<LatLng>()
        private set

     */

    private var _usvPath = mutableStateListOf<LatLng>()
    var usvPath: List<LatLng> = _usvPath

    var usvRTPos = mutableStateListOf<LatLng>()
        private set

    fun onOpenButtonClick(context: Context){
        viewModelScope.launch {
            // process Open Plan button click
            usvPath.toMutableList().apply { clear() }
            Log.i("Open plan", "loading default usv path")
            try{
                val filename = "usv.path"
                val json = loadUSVPath(context, filename)
                val obj = Json.decodeFromString<List<USVNode>>(json)
                obj.forEach {
                    //usvPath.add(USVNode(it.latitude, it.longitude))
                    usvPath.toMutableList().apply { add(LatLng(it.Latitude, it.Longitude)) }
                }
                Log.i("Open plan", "loading default usv path succesfully")
            }
            catch(e: Exception){
                Log.e("loading USV path - Exception caught", e.toString())
            }
        }
    }

    fun onSaveButtonClick(context: Context){
        viewModelScope.launch {
            // process Save Plan button click

            Log.i("Save plan", "saving usv path")
            try{
                val jsonValues = mutableListOf<USVNode>()
                usvPath.toList().forEach {
                    jsonValues.add(USVNode(it.latitude, it.longitude))
                }
                val json = Json.encodeToString(jsonValues)

                val filename = "usv.path"
                saveUSVPath(context, filename, json)
                Log.i("Save plan", "saving usv path succesfully")
            } catch (e: Exception){
                Log.e("saving USV path - Exception caught", e.toString())
            }
        }
    }

    fun onMapClick(clickPoint: LatLng){
        //usvPath.toMutableList().apply { add(clickPoint) }
        _usvPath.add(clickPoint)
        Log.i("onMapClick", "clickPoint ${clickPoint}")
    }

    fun onClearButton() {
        //usvPath.toMutableList().apply { clear() }
        _usvPath.clear()
    }

    fun onNewPosition(receivedPos: LatLng){
        usvRTPos.add(receivedPos)
        Log.i("onNewPosition", "received Position ${receivedPos}")
    }
}

class PollutionVM : androidx.lifecycle.ViewModel(){
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
                try{
                    val pollution = getUsvPollution()
                    handleUSVPollutionResult(pollution)
                    var gps = getUsvGps()
                    saveUsvData(gps, pollution)
                } catch(e: Exception){
                    Log.e("Pollution onButtonClick", "getUsvPollution exception")
                }
            }
        }
    }

    fun onContinuousMonitoring() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                getUsvData()
            }
        }
    }

    private fun handleUSVPollutionResult(pollution: Pollution) {
        pressure = pollution.Pressmbar.toDouble()
        temperature = pollution.TempCx100.toDouble() / 100
        pH = pollution.pHx100.toDouble() / 100
        orp = pollution.ORPmVx10.toDouble() / 10
    }
}

@Composable
fun showPlanningMenu(context: Context){

    val viewModel: PathOpVM = viewModel()

    Row(){
        Button(onClick = {
            viewModel.onOpenButtonClick(context)
        }){
            Text("Open plan")
        }
        Button(onClick = {
            viewModel.onSaveButtonClick(context)
        }){
            Text("Save plan")
        }
    }
}

@Composable
fun showMonitoringMenu(context: Context){
    val dButton = 25.dp
    Row {
        // Remote control
        Column(){
            FloatingActionButton(onClick = {  },
                modifier = Modifier
                    .size(dButton, dButton)) {
                androidx.compose.material3.Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Forward")
            }
            Row() {
                FloatingActionButton(onClick = {  },
                    modifier = Modifier
                        .size(dButton, dButton)) {
                    androidx.compose.material3.Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Left")
                }
                FloatingActionButton(onClick = {  },
                    modifier = Modifier
                        .size(dButton, dButton)) {
                    androidx.compose.material3.Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Right")
                }
            }
            FloatingActionButton(onClick = {  },
                modifier = Modifier
                    .size(dButton, dButton)) {
                androidx.compose.material3.Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Backward")
            }
        }

        // Get pollution data
        pollutionUI()
    }

}

@Composable
fun showVideoMenu(context: Context){
    val config = StreamConfig("sample",
        "10.2.5.57",
        8554,
        "mystream",
        "",
        "",
        false)

    /*
    val config = StreamConfig(getString(R.string.streamName),
        getString(R.string.rtspServer),
        getString(R.string.rtspPort),
        getString(R.string.rtspPath),
        getString(R.string.rtspUser),
        getString( R.string.rtspPassword),
        false)

     */
    VideoPlayer(config = config)
}

