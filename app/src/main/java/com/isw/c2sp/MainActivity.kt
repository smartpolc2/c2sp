package com.isw.c2sp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent


import com.isw.c2sp.ui.theme.C2UI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

class MainActivity : ComponentActivity() {
    private var coroutineScope: CoroutineScope? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            C2UI(this)

            //otherMap()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope?.cancel()
    }
}
