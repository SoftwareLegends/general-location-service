package com.gateway.glsapp

import android.graphics.Color
import android.os.Bundle
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.gateway.gls.di.GLSInitializer
import com.gateway.gls.domain.entities.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


class MainActivity : ComponentActivity(), GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {
    private var map: GoogleMap? = null
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermissions()
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { map = it }
        testGLSLibrary()
    }

    private fun moveCameraToUser(pathPoint: List<LatLng>) {
        if (pathPoint.isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoint.last(),
                    16.5f
                )
            )
        }
    }

    private fun addLatestPolyline(pathPoint: List<LatLng>) {
        if (pathPoint.isNotEmpty() && pathPoint.size > 1) {
            val preLastLatLng = pathPoint[pathPoint.lastIndex - 1]
            val lastLatLng = pathPoint.last()
            val polylineOption = PolylineOptions()
                .color(Color.RED)
                .width(21f)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOption)
        }
    }

    private fun testGLSLibrary() {
        val glsManager = GLSInitializer(applicationContext).create()

        val result = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) {}

        glsManager.configureLocationRequest(
            maxUpdates = Int.MAX_VALUE,
            intervalMillis = 0,
            maxUpdateDelayMillis = 0,
            minUpdateDistanceMeters = 1f,
            minUpdateIntervalMillis = 0,
            priority = Priority.HighAccuracy
        )

        glsManager.requestLocationSettings(result)
        val positions = mutableListOf<LatLng>()
        CoroutineScope(Dispatchers.IO).launch {
            glsManager.requestLocationUpdatesAsFlow()
                .collect { resource ->
                    resource.toData?.let {
                        val pos = LatLng(it.latitude, it.longitude)
                        positions.add(pos)
                    }

                    withContext(Dispatchers.Main) { addLatestPolyline(positions) }
                    withContext(Dispatchers.Main) { moveCameraToUser(positions) }
                    Timber.d("\nLOCATIONS: (${resource.toData?.longitude}, ${resource.toData?.latitude}) -> ${resource.toData?.accuracy}m\n")
                }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), 100
        )
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map?.apply {
            isMyLocationEnabled = true
            isBuildingsEnabled = true
            isIndoorEnabled = true
            isTrafficEnabled = true
        }
        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMyLocationClickListener(this)
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
            .show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG)
            .show()
    }
}
