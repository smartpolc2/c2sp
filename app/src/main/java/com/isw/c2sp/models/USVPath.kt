package com.isw.c2sp.models

import kotlinx.serialization.Serializable

@Serializable
data class USVNode (
    var latitude: Double,
    var longitude: Double
)