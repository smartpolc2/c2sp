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

class USVData(
    var gps: USVGps,
    var pollution: Pollution
)

class hUnits(
    var time: String,
    var temperature_2m: String
)
class hDataArray(
    var time: ArrayList<String>,
    var temperature_2m: ArrayList<Double>
)
class WeatherData(
    var latitude: Double,
    var longitude: Double,
    var generationtime_ms: Double,
    var timezone: String,
    var timezone_abbreviation: String,
    var elevation: Double,
    var hourly_units: hUnits,
    var hourly: hDataArray
)