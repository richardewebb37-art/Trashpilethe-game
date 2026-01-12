package com.trashapp.libgdx

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.trashapp.TrashApplication
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AndroidLauncher : AndroidApplication() {
    
    @Inject
    lateinit var trashGame: TrashGame
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val config = AndroidApplicationConfiguration().apply {
            useAccelerometer = false
            useCompass = false
            useGyroscope = false
            hideStatusBar = true
            useImmersiveMode = true
        }
        
        initialize(trashGame, config)
    }
}