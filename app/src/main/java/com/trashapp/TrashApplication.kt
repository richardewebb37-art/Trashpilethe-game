package com.trashapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TrashApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase and other services here
    }
}