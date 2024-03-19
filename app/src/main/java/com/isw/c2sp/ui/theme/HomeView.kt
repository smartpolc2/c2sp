package com.isw.c2sp.ui.theme

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlin.random.Random

class HomeView{
    private val _elements = mutableStateListOf<Int>()
    val elements: List<Int> = _elements

    fun addElement(){
        val randomNumber = Random.nextInt(from = 1, until = 100)
        _elements.add(randomNumber)
        Log.d("HomeViewModel", "Added number: ${_elements.last()}")
    }
}

class PathView{
    private val _elements = mutableStateListOf<LatLng>()
    val elements: List<LatLng> = _elements

    fun addElement(){
        val randomNumber = Random.nextDouble(from = 40.0, until = 60.0)
        _elements.add(LatLng(randomNumber, randomNumber))
        Log.d("PathViewModel", "Added number: ${_elements.last()}")
    }

    fun add(node: LatLng){
        _elements.add(node)
        Log.d("PathViewModel",
            "Add node: ${_elements.last().latitude}, ${_elements.last().longitude}")
    }
}