package com.shalatan.devjoke

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.ThemeDevJoke)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener {
//            if (!it.isSuccessful) {
//                Log.e("FETCHING FCM REGISTRATION TOKEN FAILED", it.exception.toString())
//                return@OnCompleteListener
//            }
//            val token = it.result
//            Log.e("FETCHING FCM REGISTRATION TOKEN SUCCESS", token.toString())
//        })

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        FirebaseApp.initializeApp(this)
    }
}