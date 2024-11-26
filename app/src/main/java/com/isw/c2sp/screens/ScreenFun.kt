package com.isw.c2sp.screens

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapProperties
import com.isw.c2sp.utils.checkForPermission
import com.isw.c2sp.utils.getCurrentLocation
import com.isw.c2sp.utils.simUsvPos

@Composable
fun C2Screen(context: Context){
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        var hasLocationPermission by remember {
            mutableStateOf(checkForPermission(context))
        }

        if (hasLocationPermission) {
            MapScreen(context)
        } else {
            LocationPermissionScreen {
                hasLocationPermission = true
            }
        }
    }
}

@Composable
fun MapScreen(context: Context){
    var showMap by remember { mutableStateOf(false) }
    var c2Loc by remember { mutableStateOf(LatLng(0.0,0.0)) }
    var usvLoc by remember { mutableStateOf(LatLng(0.0,0.0)) }
    var mapProperties by remember { mutableStateOf(MapProperties()) }

    var newNode by remember {
        mutableStateOf<Boolean?>(null)    }

    getCurrentLocation(context) {
        showMap = true
        c2Loc = it
    }

    val usvPath = mutableListOf<LatLng>()

    usvLoc = simUsvPos(c2Loc)

    if (showMap)
    {
        //display C2 map
        c2MapUI(context = context,
            c2Pos = c2Loc,
            usvPos = usvLoc,
            mapProperties
        )

        //c2MainScreen()
    }
    else{
        Text(text = "Loading map...")
    }
}