package com.smartfitness.app.ui.tracking.ui

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.animation.LinearInterpolator
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.smartfitness.app.core.utilities.AppLog
import com.smartfitness.app.core.utilities.HelperFunctions
import com.smartfitness.app.core.utilities.MarkerAnimator
import com.smartfitness.app.ui.tracking.data.LocationRepository
import com.smartfitness.app.ui.tracking.model.RiderLocation
import com.smartfitness.app.ui.tracking.model.RouteSegment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val repo: LocationRepository
) : ViewModel() {
    private val _animatedLatLng = MutableStateFlow<LatLng?>(null)
    val animatedLatLng: StateFlow<LatLng?> = _animatedLatLng
    private val _bearing = MutableStateFlow(0f)
    val bearing: StateFlow<Float> = _bearing

    private var lastLatLng: LatLng? = null
    private val animator = MarkerAnimator()

    private val _location = MutableStateFlow<RiderLocation?>(null)
    val location: StateFlow<RiderLocation?> = _location

    private val _route = MutableStateFlow<List<RouteSegment>>(emptyList())
    val route: StateFlow<List<RouteSegment>> = _route
    private val _eta = MutableStateFlow("")
    val eta: StateFlow<String> = _eta

    private val _distance = MutableStateFlow("")
    val distance: StateFlow<String> = _distance

    private val _isTrackingStarted = MutableStateFlow(false)
    val isTrackingStarted: StateFlow<Boolean> = _isTrackingStarted

    fun startTracking() {
        if (_isTrackingStarted.value) return

        val riderId = "rider_123"
        _isTrackingStarted.value = true

        // 👇 Listen Firebase updates (receiver side)
        repo.listenLocation(riderId) { location ->
            onNewLocation(location)
        }
    }

    fun startListening(riderId: String) {
        repo.listenLocation(riderId) {
            _location.value = it
        }
    }

    private var lastRouteFetchTime = 0L
    private var lastRoutePoint: LatLng? = null
    private val _destination = MutableStateFlow<LatLng?>(null)
    val destination: StateFlow<LatLng?> = _destination

    fun setDestination(address: String, context: android.content.Context) {
        viewModelScope.launch {
            try {
                val geocoder = android.location.Geocoder(context)
                // Use the modern Geocoder API if possible, but for simplicity we use the sync one here
                // Note: getFromLocationName is deprecated but still works for now. 
                // In a real app, use the callback-based version for API 33+.

                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(address, 1)
                AppLog.v(message = "Geocoding result for '$address': $addresses")
                if (addresses?.isNotEmpty() == true) {
                    val lat = addresses[0].latitude
                    val lng = addresses[0].longitude
                    val latLng = LatLng(lat, lng)
                    _destination.value = latLng
                    
                    // Save to Firestore
                    repo.saveDestination("rider_123", address, lat, lng)
                    
                    // Path calculation removed from here. 
                    // Path will only be drawn once "Start Tracking" is clicked and we receive location updates.
                }
            } catch (e: Exception) {
                _routeError.value = "Geocoding failed: ${e.message}"
            }
        }
    }

    fun onNewLocation(location: RiderLocation) {
        val newLatLng = LatLng(location.lat, location.lng)
        
        // Sync destination from Firestore
        if (location.destinationLat != null && location.destinationLng != null) {
            _destination.value = LatLng(location.destinationLat, location.destinationLng)
        }

// ✅ Always update marker + animation
        animateMarker(newLatLng)
        // ✅ Decide whether to refresh route
        _destination.value?.let { dest ->
            loadRoute(newLatLng, dest)
        }
       /* if (shouldUpdateRoute(newLatLng)) {
            loadRoute(newLatLng, destination)
            lastRouteFetchTime = System.currentTimeMillis()
            lastRoutePoint = newLatLng
        }*/
        lastLatLng?.let { prev ->
            _bearing.value = HelperFunctions.getBearing(prev, newLatLng)
        }

        animator.animate(lastLatLng ?: newLatLng, newLatLng) {
            _animatedLatLng.value = it
        }

        lastLatLng = newLatLng
    }

    fun shouldUpdateRoute(newLatLng: LatLng): Boolean {

        // First time
        if (lastRoutePoint == null) return true

        val results = FloatArray(1)
        android.location.Location.distanceBetween(
            lastRoutePoint!!.latitude,
            lastRoutePoint!!.longitude,
            newLatLng.latitude,
            newLatLng.longitude,
            results
        )

        val movedDistance = results[0]
        val timePassed = System.currentTimeMillis() - lastRouteFetchTime

        return movedDistance > 100 || timePassed > 20000
    }


    fun animateMarker(newLatLng: LatLng) {

        val start = lastLatLng ?: newLatLng

        val handler = Handler(Looper.getMainLooper())
        val startTime = SystemClock.uptimeMillis()
        val duration = 1000L

        val interpolator = LinearInterpolator()

        handler.post(object : Runnable {
            override fun run() {
                val elapsed = SystemClock.uptimeMillis() - startTime
                val t = (elapsed.toFloat() / duration).coerceAtMost(1f)

                val lat = start.latitude + (newLatLng.latitude - start.latitude) * t
                val lng = start.longitude + (newLatLng.longitude - start.longitude) * t

                _animatedLatLng.value = LatLng(lat, lng)

                if (t < 1f) {
                    handler.postDelayed(this, 16)
                }
            }
        })
    }

    private val _routeError = MutableStateFlow<String?>(null)
    val routeError: StateFlow<String?> = _routeError

    fun loadRoute(origin: LatLng, dest: LatLng) {
        viewModelScope.launch {
            try {
                val result = repo.fetchRoute(
                    "${origin.latitude},${origin.longitude}",
                    "${dest.latitude},${dest.longitude}"
                )
                _route.value = result.segments
                _eta.value = result.durationText
                _distance.value = result.distanceText
                _routeError.value = null
            } catch (e: Exception) {
                AppLog.v(message = "Route fetch failed: ${e.message}")
                _routeError.value = e.message ?: "Failed to load route"
            }
        }
    }
}