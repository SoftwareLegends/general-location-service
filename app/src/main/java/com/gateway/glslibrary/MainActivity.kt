package com.gateway.glslibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gateway.glslibrary.di.LocationServiceModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        testGLSLibrary()
    }

    private fun testGLSLibrary(){
        LocationServiceModule.locationRepository.run {
            Timber.d("is Location Service Available: $isLocationServicesAvailable")
            CoroutineScope(Dispatchers.IO).launch {
                lastLocation().collect {
                    Timber.d(it.toString().substringAfter('$'))
                }
            }
        }
    }
}