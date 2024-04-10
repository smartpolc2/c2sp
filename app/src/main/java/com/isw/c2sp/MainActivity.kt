package com.isw.c2sp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.isw.c2sp.screens.C2Screen
//import com.isw.c2sp.ui.theme.C2UI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

class MainActivity : ComponentActivity() {
    private var coroutineScope: CoroutineScope? = null

    //Firebase authentication
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = Firebase.auth

        //reload()
        val email = "mpalade@gmail.com"
        val password = "c2pollution"
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){task->
                if(task.isSuccessful){
                    // signin succesfull
                    Log.i("signin", "signInWithEmail:success")
                    val user = auth.currentUser
                }
                else{
                    //signin fail
                    Log.i("signin", "signInWithEmail:failure", task.exception)
                }
            }

        setContent {
            C2Screen(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope?.cancel()
    }
}
