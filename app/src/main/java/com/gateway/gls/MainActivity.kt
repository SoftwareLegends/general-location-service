package com.gateway.gls

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gateway.gls.di.GLServiceLocator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        testGLSLibrary()
    }

    private fun testGLSLibrary(){
        GLServiceLocator.locationRepository.run {
            Timber.d("is Location Service Available: $isLocationServicesAvailable")
            CoroutineScope(Dispatchers.IO).launch {
                requestLocationUpdates().collect {
                    Timber.d(it.toString().substringAfter('$'))
                }
            }
        }
    }
}