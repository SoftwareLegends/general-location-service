package com.gateway.gls

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.gateway.gls.di.GLSInitializer
import com.gateway.gls.domain.entities.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        testGLSLibrary()
    }

    private fun testGLSLibrary() {
        val glsManager = GLSInitializer(applicationContext).create()

        val result = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) {

        }
        glsManager.configureLocationRequest(
            maxUpdates = Int.MAX_VALUE,
            intervalMillis = 1000,
            maxUpdateDelayMillis = 1000,
            minDistanceThreshold = 0f,
            minUpdateIntervalMillis = 0,
            priority = Priority.HighAccuracy
        )

        glsManager.requestLocationSettings(result)

        CoroutineScope(Dispatchers.IO).launch {
            glsManager.requestLocationUpdatesAsFlow()
                .collect {
                    Timber.d("\nLOCATIONS: (${it.toData?.longitude}, ${it.toData?.latitude}) -> ${it.toData?.accuracy}m\n")
                }
        }
    }
}
