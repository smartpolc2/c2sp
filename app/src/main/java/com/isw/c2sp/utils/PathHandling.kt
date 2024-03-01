package com.isw.c2sp.utils

import android.content.Context
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStreamReader

fun saveUSVPath(context: Context, filename: String, fileContents: String) {
    try {
        val outputStream: FileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
        outputStream.write(fileContents.toByteArray())
        outputStream.close()
        println("Content written to $filename")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun loadUSVPath(context: Context, filename: String): String{
    val stringBuilder = StringBuilder()
    try {
        val inputStream = context.openFileInput(filename)
        val inputStreamReader = InputStreamReader(inputStream)
        val bufferedReader = BufferedReader(inputStreamReader)
        var line: String? = bufferedReader.readLine()
        while (line != null) {
            stringBuilder.append(line).append("\n")
            line = bufferedReader.readLine()
        }
        inputStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return stringBuilder.toString()
}