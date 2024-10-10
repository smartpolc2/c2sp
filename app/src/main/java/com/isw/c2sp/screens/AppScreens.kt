package com.isw.c2sp.screens


import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.twotone.KeyboardArrowRight
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.isw.c2sp.R
import com.isw.c2sp.utils.getUsvGps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


@Composable
fun MenuComposable(
    dynamicMenu: @Composable (context: Context) -> Unit
) {
    val context = LocalContext.current
    dynamicMenu(context)
}

@Composable
fun ContentComposable(
    trackPlanned: List<LatLng>,
    trackReal: List<LatLng>,
    dynamicContent: @Composable (trackPlanned: List<LatLng>, trackReal: List<LatLng>) -> Unit
){
    dynamicContent(
        trackPlanned,
        trackReal
    )
}

//@Preview
@Composable
fun NoMenu(context: Context){}

@Composable
fun NoContent(trackPlanned: List<LatLng>, trackReal: List<LatLng>){
    /*
    trackPlanned.forEach{
        Polyline(points = trackPlanned,
            clickable = true,
            color = Color.Red,
            width = 5f
        )
    }

    trackReal.forEach {
        Polyline(points = trackReal,
            clickable = true,
            color = Color.Green,
            width = 5f
        )
    }

     */
}

@Composable
fun DisplayTrack(trackPlanned: List<LatLng>, trackReal: List<LatLng>){
    Polyline(points = trackPlanned,
        clickable = true,
        color = Color.Red,
        width = 5f
    )
}


//@Preview
@Composable
fun PlanningMenu(context: Context){
    val viewModel: PathOpVM = viewModel()
    Row{
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
fun PlanningContent(trackPlanned: List<LatLng>, trackReal: List<LatLng>){
    trackPlanned.forEach{
        Polyline(points = trackPlanned,
            clickable = true,
            color = Color.Red,
            width = 5f
        )
    }

    /*
    trackReal.forEach {
        Polyline(points = trackReal,
            clickable = true,
            color = Color.Green,
            width = 5f
        )
    }
     */
}

@Composable
fun MonitoringContent(trackPlanned: List<LatLng>, trackReal: List<LatLng>){
    /*
    trackPlanned.forEach{
        Polyline(points = trackPlanned,
            clickable = true,
            color = Color.Red,
            width = 5f
        )
    }
     */

    trackReal.forEach {
        Polyline(points = trackReal,
            clickable = true,
            color = Color.Green,
            width = 5f
        )
    }
}


//@Preview
@Composable
fun MonitoringMenu(context: Context){
    val dButton = 50.dp
    val viewModel: PathOpVM = viewModel()
    Row {
        // Remote control
        Column{
            FloatingActionButton(onClick = { viewModel.onRemoteCommand(context) },
                modifier = Modifier
                    .size(dButton, dButton)) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Forward")
            }
            Row {
                FloatingActionButton(onClick = { viewModel.onRemoteCommand(context) },
                    modifier = Modifier
                        .size(dButton, dButton)) {
                    Icon(Icons.AutoMirrored.TwoTone.KeyboardArrowLeft, contentDescription = "Left")
                }
                FloatingActionButton(onClick = { viewModel.onRemoteCommand(context) },
                    modifier = Modifier
                        .size(dButton, dButton)) {
                    Icon(Icons.AutoMirrored.TwoTone.KeyboardArrowRight, contentDescription = "Right")
                }
            }
            FloatingActionButton(onClick = { viewModel.onRemoteCommand(context) },
                modifier = Modifier
                    .size(dButton, dButton)) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Backward")
            }
        }

        // Get pollution data
        // send context for webservice call
        pollutionUI(context)
    }
}

//@Preview
@Composable
fun VideoMenu(context: Context){
    val config = StreamConfig("usv",
        "10.2.5.57",
        8554,
        "mihai",
        "",
        "",
        true)

    /*
    val config = StreamConfig(getString(R.string.streamName),
        getString(R.string.rtspServer),
        getString(R.string.rtspPort),
        getString(R.string.rtspPath),
        getString(R.string.rtspUser),
        getString( R.string.rtspPassword),
        false)

     */
    //VideoPlayer(config = config)
    VidePlayerSimple(config = config)
}
fun saveAddress(context: Context, key: String, address: String) {
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putString(key, address)
        apply()
    }
}

fun loadAddress(context: Context, key: String, default: String): String {
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    return sharedPreferences.getString(key, default) ?: default
}

@Composable
fun SettingsMenu(context: Context) {
    Column {
        AddressInputField(
            context = context,
            label = "USV Address",
            preferenceKey = "usvSimAddress",
            defaultValue = "127.0.0.1:8080"
        )
        AddressInputField(
            context = context,
            label = "AIS Address",
            preferenceKey = "aisSimAddress",
            defaultValue = "127.0.0.1:8080"
        )
        AddressInputField(
            context = context,
            label = "Meteo Address",
            preferenceKey = "meteoSimAddress",
            defaultValue = "127.0.0.1:8080"
        )
    }
}

@Composable
fun AddressInputField(context: Context, label: String, preferenceKey: String, defaultValue: String) {
    // Load the persisted value on initial render
    var address by remember {
        mutableStateOf(loadAddress(context, preferenceKey, defaultValue))
    }

    // TextField for user input
    TextField(
        value = address,
        onValueChange = {
            address = it
            saveAddress(context, preferenceKey, address) // Save on every change
        },
        label = { Text(label) }
    )
}


@Composable
fun C2MainScreen(context: Context){
    val c2MarkerLat = loadAddress(context, "c2Lat", "44.449762").toDouble()
    val c2MarkerLon = loadAddress(context, "c2Lon", "26.041717").toDouble()
    val c2Marker by remember{
        mutableStateOf(LatLng(c2MarkerLat, c2MarkerLon))
    }
    val cameraPositionState = rememberCameraPositionState{
        position = CameraPosition.fromLatLngZoom(c2Marker, 15f)
    }
    //var usvMarker = generateNewPosition(c2Marker)
    var usvMarker by remember{
        mutableStateOf(LatLng(44.449762, 26.041717))
    }
    var aisMarker by remember{
        mutableStateOf(LatLng(44.449762, 26.041717))
    }

    val pathViewModel: PathOpVM = viewModel()
    val usvPath = pathViewModel.usvPath
    val usvRTPos = pathViewModel.usvRTPos

    val usvLastPosLog = getString(context, R.string.usvLastPosLog).toInt()
    var realTrack by remember { mutableStateOf(emptyList<LatLng>()) }

    //usv planned path
    /*
    var usvPath = remember {
        mutableStateListOf(LatLng(44.449762, 26.041717))
    }
    var clickedPoints by remember { mutableStateOf(emptyList<LatLng>()) }
    //var polyline by remember { mutableStateOf(emptyList<LatLng>()) }

     */

    var dynamicMenu by remember {
        mutableStateOf<@Composable () -> Unit>({ NoMenu(context) })
    }
    var dynamicContent by remember {
        mutableStateOf<@Composable (List<LatLng>, List<LatLng>) -> Unit>({usvPath, usvRTPos ->
            NoContent(
                trackPlanned = usvPath,
                trackReal = usvRTPos)
        })
    }

    LaunchedEffect(key1 = Unit) {
        while (true) {
            val result = withContext(Dispatchers.IO){
                // detect usv presence
                try {
                    val knownUSVPos = getUsvGps(context)
                    Log.i("knownUSVPos", knownUSVPos.Latitude.toString() + " " + knownUSVPos.Longitude.toString())

                    //usvMarker = generateNewPosition(LatLng(knownUSVPos.Latitude, knownUSVPos.Longitude))
                    usvMarker = LatLng(knownUSVPos.Latitude / 100, knownUSVPos.Longitude / 100)
                    //pathViewModel.onNewReceivedPosition(receivedPos = usvMarker)
                    pathViewModel.onNewReceivedPosition(receivedPos = LatLng(knownUSVPos.Latitude / 100, knownUSVPos.Longitude / 100) )

                    /*
                    realTrack = realTrack.toMutableList().apply { add(usvMarker) }
                    if (realTrack.size > usvLastPosLog){
                        realTrack = realTrack.toMutableList().apply { removeFirst() }
                    }

                     */

                    //aisMarker = generateNewPosition(usvMarker)

                    Log.i("usvMarker", usvMarker.toString())
                } catch (e: Exception) {
                    // failed connection
                    Log.e("C2MainScreen", "Usv API not available")
                }
            }
            delay(5*1000)
        }

    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){
        Box {
            GoogleMap(
                cameraPositionState = cameraPositionState,
                onMapClick = {
                    clickPoint ->
                    /*
                        usvPath.add(clickPoint)
                    clickedPoints = clickedPoints.toMutableList().apply { add(clickPoint) }

                     */
                    pathViewModel.onMapClick(clickPoint = clickPoint)
                    //Log.i("GoogleMap", "clicked point = " + clickedPoints.size.toString())
                }
            ){
                Marker(
                    state = MarkerState(c2Marker),
                    title = "C2",
                    snippet = "(${c2Marker.latitude},${c2Marker.longitude})",
                    icon = BitmapDescriptorFactory.fromResource(R.mipmap.c2)
                )

                Marker(
                    state = MarkerState(usvMarker),
                    title = "USV",
                    snippet = "(${usvMarker.latitude},${usvMarker.longitude})",
                    icon = BitmapDescriptorFactory.fromResource(R.mipmap.usv)
                )


                /*
                Marker(
                    state = MarkerState(aisMarker),
                    title = "AIS",
                    snippet = "(${aisMarker.latitude},${aisMarker.longitude})",
                    icon = BitmapDescriptorFactory.fromResource(R.mipmap.usv)
                )

                 */

                ContentComposable(trackPlanned = usvPath,
                    trackReal = usvRTPos,
                    dynamicContent = dynamicContent
                )

                /*
                usvPath.forEach{
                    Polyline(points = usvPath,
                        color = Color.Blue)
                }

                usvRTPos.forEach{
                    Polyline(points = usvRTPos,
                        color = Color.Green)
                }
                 */
            }

            Column{
                Row{
                    FloatingActionButton(onClick = {
                        dynamicMenu = { PlanningMenu(context) }
                        //clickedPoints = clickedPoints.toMutableList().apply { clear() }
                        dynamicContent = { usvPath, usvRTPos ->
                            PlanningContent(trackPlanned = usvPath, trackReal = usvRTPos)
                        }
                        pathViewModel.resetPlanning()
                    },
                        modifier = Modifier
                            .size(30.dp, 30.dp))
                    {
                        Icon(
                            Icons.Default.Build,
                            contentDescription = "Planning"
                        )
                    }
                    FloatingActionButton(onClick = {
                        dynamicMenu = { MonitoringMenu(context) }
                        dynamicContent = { usvPath, usvRTPos ->
                            MonitoringContent(trackPlanned = usvPath, trackReal = usvRTPos)
                        }
                        pathViewModel.resetPlanning()
                        pathViewModel.resetTracking()
                    },
                        modifier = Modifier
                            .size(30.dp, 30.dp))
                    {
                        Icon(
                            Icons.Default.Place,
                            contentDescription = "Monitoring"
                        )
                    }
                    FloatingActionButton(onClick = {dynamicMenu = { VideoMenu(context) } },
                        modifier = Modifier
                            .size(30.dp, 30.dp))
                    {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Video"
                        )
                    }

                    FloatingActionButton(onClick = {dynamicMenu = {SettingsMenu(context)}},
                        modifier = Modifier
                            .size(30.dp, 30.dp))
                    {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }

                MenuComposable {
                    dynamicMenu()
                }
            }
        }
    }
}


