package com.isw.c2sp.utils

import android.util.Log
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.isw.c2sp.models.Pollution
import com.isw.c2sp.models.USVData
import com.isw.c2sp.models.USVGps
import java.io.InputStreamReader
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.net.ssl.HttpsURLConnection

fun getUsvPollution():Pollution{
    //val result = withContext(Dispatchers.IO) {
        val url =
            URL("https://a5043b0f-1c90-4975-9da3-1f297cac6676.mock.pstmn.io/api/polution/getPolPar/")
        val connection = url.openConnection() as HttpsURLConnection

        if (connection.responseCode == 200) {
            val inputSystem = connection.inputStream
            val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
            val request = Gson().fromJson(inputStreamReader, Pollution::class.java)

            // somethig upon request
            return request

            inputStreamReader.close()
            inputSystem.close()
        } else {
            // failed connection
            Log.e("getUsvPollution", "Usv API not available")
            throw Exception("Usv API not available")
        }
    //}
}

fun getUsvGps():USVGps{
    val url =
        URL("https://a5043b0f-1c90-4975-9da3-1f297cac6676.mock.pstmn.io/api/polution/getGpsPar/")
    val connection  = url.openConnection() as HttpsURLConnection

    if(connection.responseCode == 200)
    {
        val inputSystem = connection.inputStream
        val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
        val request = Gson().fromJson(inputStreamReader, USVGps::class.java)

        // somethig upon request
        return request

        inputStreamReader.close()
        inputSystem.close()
    }
    else
    {
        // failed connection
        Log.e("getUsvGps", "Usv API not available")
        throw Exception("Usv API not available")
    }
}

fun getUsvData(){
    try{
        var gps = getUsvGps()
        var pollution = getUsvPollution()
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