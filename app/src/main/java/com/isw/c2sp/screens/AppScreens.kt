package com.isw.c2sp.screens


import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.isw.c2sp.R
import com.isw.c2sp.utils.getUsvGps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


@Composable
fun MenuComposable(
    dynamicMenu: @Composable () -> Unit
) {
    dynamicMenu()
}

@Preview
@Composable
fun NoMenu(){}


@Preview
@Composable
fun PlanningMenu(){
    val viewModel: PathOpVM = viewModel()
    Row(){
        Button(onClick = {
            //viewModel.onOpenButtonClick(context)
        }){
            Text("Open plan")
        }
        Button(onClick = {
            //viewModel.onSaveButtonClick(context)
        }){
            Text("Save plan")
        }
    }
}

@Preview
@Composable
fun MonitoringMenu(){
    val dButton = 50.dp
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

@Preview
@Composable
fun VideoMenu(){
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

@Composable
fun C2MainScreen(context: Context){

    var c2Marker by remember{
        mutableStateOf(LatLng(44.426395, 26.0986619))
    }
    val cameraPositionState = rememberCameraPositionState{
        position = CameraPosition.fromLatLngZoom(c2Marker, 15f)
    }
    //var usvMarker = generateNewPosition(c2Marker)
    var usvMarker by remember{
        mutableStateOf(LatLng(44.426395, 26.0986619))
    }

    LaunchedEffect(key1 = Unit) {
        while (true) {
            val result = withContext(Dispatchers.IO){
                // detect usv presence
                try {
                    val knownUSVPos = getUsvGps()
                    usvMarker = generateNewPosition(usvMarker)

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
            GoogleMap(){
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
            }

            var dynamicMenu by remember {
                mutableStateOf<@Composable () -> Unit>({ NoMenu() })
            }

            Row(){
                FloatingActionButton(onClick = {dynamicMenu = { PlanningMenu() } },
                    modifier = Modifier
                        .size(30.dp, 30.dp))
                {
                    Icon(
                        Icons.Default.Build,
                        contentDescription = "Planning"
                    )
                }
                FloatingActionButton(onClick = {dynamicMenu = { MonitoringMenu() } },
                    modifier = Modifier
                        .size(30.dp, 30.dp))
                {
                    Icon(
                        Icons.Default.Place,
                        contentDescription = "Monitoring"
                    )
                }
                FloatingActionButton(onClick = {dynamicMenu = { VideoMenu() } },
                    modifier = Modifier
                        .size(30.dp, 30.dp))
                {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Video"
                    )
                }

                MenuComposable {
                    dynamicMenu()
                }
            }
        }
    }
}


