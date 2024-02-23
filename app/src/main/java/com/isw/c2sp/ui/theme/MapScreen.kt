package com.isw.c2sp.ui.theme

import android.app.admin.DevicePolicyManager.InstallSystemUpdateCallback
import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
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
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.isw.c2sp.models.USVGps
import com.isw.c2sp.utils.checkForPermission
import com.isw.c2sp.utils.getCurrentLocation
import com.isw.c2sp.utils.simUsvPos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

@Composable
fun C2UI(context: Context){
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

    var newNode by remember {
        mutableStateOf<Boolean?>(null)    }

    getCurrentLocation(context) {
        showMap = true
        c2Loc = it
    }

    usvLoc = simUsvPos(c2Loc)

    if (showMap)
    {
        //display C2 map
        C2MapUI(context = context,
            c2Pos = c2Loc,
            usvPos = usvLoc,
            newNode = newNode,
            onNewNodeClick = { newNode = it  }
        )
    }
    else{
        Text(text = "Loading map...")
    }
}