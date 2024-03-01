package com.isw.c2sp.models

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable

@Serializable
data class USVNode (
    var Latitude: Double,
    var Longitude: Double
)