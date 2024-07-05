package com.isw.c2sp.utils

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat.getString
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.gson.Gson
import com.isw.c2sp.R
import com.isw.c2sp.models.Pollution
import com.isw.c2sp.models.USVData
import com.isw.c2sp.models.USVGps
import com.isw.c2sp.models.WeatherData
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.net.ssl.HttpsURLConnection

fun getUsvPollution(context: Context):Pollution{
    //val result = withContext(Dispatchers.IO) {
        val wspath = getString(context, R.string.pollution)
        val url =
            //URL("https://a5043b0f-1c90-4975-9da3-1f297cac6676.mock.pstmn.io/api/polution/getPolPar/")
            //URL("http://10.2.5.99:8080/pollution")
            URL(wspath)
        //val connection = url.openConnection() as HttpsURLConnection
        val connection = url.openConnection() as HttpURLConnection

        if (connection.responseCode == 200) {
            val inputSystem = connection.inputStream
            val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
            val request = Gson().fromJson(inputStreamReader, Pollution::class.java)

            inputStreamReader.close()
            inputSystem.close()

            // somethig upon request
            return request
        } else {
            // failed connection
            Log.e("getUsvPollution", "Usv API not available")
            throw Exception("Usv API not available")
        }
    //}
}

fun getUsvGps(context: Context):USVGps{
    val wspath = getString(context, R.string.gps)
    val url =
        //URL("https://a5043b0f-1c90-4975-9da3-1f297cac6676.mock.pstmn.io/api/polution/getGpsPar/")
        //URL("http://10.2.5.99:8080/gps")
        URL(wspath)
    //val connection  = url.openConnection() as HttpsURLConnection
    val connection  = url.openConnection() as HttpURLConnection

    if(connection.responseCode == 200)
    {
        val inputSystem = connection.inputStream
        val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
        val request = Gson().fromJson(inputStreamReader, USVGps::class.java)

        inputStreamReader.close()
        inputSystem.close()

        // somethig upon request
        return request
    }
    else
    {
        // failed connection
        Log.e("getUsvGps", "Usv API not available")
        throw Exception("Usv API not available")
    }
}

fun getWeather(context: Context): WeatherData {
    val wspath = "https://api.open-meteo.com/v1/forecast?latitude=44.4323&longitude=26.1063&hourly=temperature_2m&timezone=auto&forecast_days=1"
    val url = URL(wspath)
    val connection  = url.openConnection() as HttpsURLConnection

    if(connection.responseCode == 200)
    {
        val inputSystem = connection.inputStream
        val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
        val request = Gson().fromJson(inputStreamReader, WeatherData::class.java)

        Log.i("getWeather",request.toString())

        inputStreamReader.close()
        inputSystem.close()

        // somethig upon request
        return request
    }
    else
    {
        // failed connection
        Log.e("getWeather", "Weather API not available")
        throw Exception("Weather API not available")
    }
}

fun getUsvData(context: Context){
    try{
        var gps = getUsvGps(context)
        var pollution = getUsvPollution(context)
        saveUsvData(gps, pollution)
    } catch(e: Exception){
        Log.e("getUsvData", "Usv API call exception")
    }
}

fun saveUsvData(gps: USVGps, pollution: Pollution){
    try{
        val currentDateTime = LocalDateTime.now()
        // Custom formatting
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedDateTime = currentDateTime.format(formatter)

        val usvData = USVData(gps, pollution)

        val database = Firebase.database("https://pollutiondb-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference(formattedDateTime)
        //val myRef = database.getReference("yyyy-MM-dd HH:mm:ss")

        myRef.setValue(usvData)
    } catch(e: Exception){
        Log.e("saveUsvData", "Firebase saving exception")
    }

}