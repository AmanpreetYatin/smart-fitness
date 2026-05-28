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

    private val _route = MutableStateFlow<List<LatLng>>(emptyList())
    val route: StateFlow<List<LatLng>> = _route
    private val _eta = MutableStateFlow("")
    val eta: StateFlow<String> = _eta

    private val _distance = MutableStateFlow("")
    val distance: StateFlow<String> = _distance

    private var isTrackingStarted = false

    fun startTracking() {
        if (isTrackingStarted) return

        val riderId = "rider_123"

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
    val destination = LatLng(30.7228,  76.7487 )

    fun onNewLocation(location: RiderLocation) {
        val newLatLng = LatLng(location.lat, location.lng)
// ✅ Always update marker + animation
        updateBearing(newLatLng)
        animateMarker(newLatLng)
        // ✅ Decide whether to refresh route
        loadRoute(newLatLng, destination)
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

    fun updateBearing(newLatLng: LatLng) {

        lastLatLng?.let { prev ->

            val latDiff = newLatLng.latitude - prev.latitude
            val lngDiff = newLatLng.longitude - prev.longitude

            val angle = Math.toDegrees(
                kotlin.math.atan2(lngDiff, latDiff)
            ).toFloat()

            _bearing.value = angle
        }
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
                _route.value = result.points
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