package com.isw.c2sp.business

import android.util.Log
import com.isw.c2sp.models.USVGps

class C2MapVM : androidx.lifecycle.ViewModel() {

    fun getUSVPos():USVGps{
        try {
            return getUSVPos()
        } catch (e: Exception){
            // failed connection
            Log.e("getUsvGps", "Usv API not available")
            throw Exception("Usv API not available")
        }
    }
}