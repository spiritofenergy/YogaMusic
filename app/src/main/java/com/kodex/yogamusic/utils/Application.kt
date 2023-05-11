package com.kodex.yogamusic.utils

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : Application() {
    override fun onCreate(){
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
        Log.d("FirebaseApp", "FirebaseApp: initializeApp")
    }
}