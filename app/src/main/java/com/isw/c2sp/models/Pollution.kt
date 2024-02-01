package com.isw.c2sp.models


class Pollution (
    var Pressmbar: Long,
    var TempCx100: Long,
    var pHx100: Long,
    var ORPmVx10: Long
)

class USVGps (
    var Latitude: Double,
    var Longitude: Double,
    var Year: Int,
    var Month: Int,
    var Hour: Int,
    var Minutes: Int,
    var Seconds: Int,
    var GpsFix: Int,
    var SatInView: Int
)