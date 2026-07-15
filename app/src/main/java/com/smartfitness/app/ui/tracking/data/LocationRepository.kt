package com.smartfitness.app.ui.tracking.data

import com.smartfitness.app.BuildConfig
import com.smartfitness.app.core.utilities.HelperFunctions
import com.smartfitness.app.ui.tracking.model.RiderLocation
import com.smartfitness.app.ui.tracking.model.RouteInfo
import com.smartfitness.app.ui.tracking.model.RouteSegment
import com.smartfitness.app.ui.tracking.model.TrafficLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val firebase: FirebaseDataSource
) {
    fun sendLocation(riderId: String, location: RiderLocation) {
        firebase.updateLocation(riderId, location)
    }

    fun listenLocation(riderId: String, onUpdate: (RiderLocation) -> Unit) {
        firebase.observeLocation(riderId, onUpdate)
    }

    fun saveDestination(riderId: String, address: String, lat: Double, lng: Double) {
        firebase.updateDestination(riderId, address, lat, lng)
    }

    suspend fun fetchRoute(origin: String, dest: String): RouteInfo = withContext(Dispatchers.IO) {

        // departure_time=now is required for traffic information
        val urlString = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=$origin&destination=$dest&departure_time=now&mode=driving&key=${BuildConfig.MAPS_API_KEY}"

        val url = URL(urlString)
        val connection = (url.openConnection() as java.net.HttpURLConnection).apply {
            requestMethod = "GET"
            // These headers allow an Android-restricted key to work with the REST API
            setRequestProperty("X-Android-Package", "com.smartfitness.app")
            setRequestProperty("X-Android-Cert", "27EE0AD8554685ECE32AB5D82AF085121AD8F114")
        }

        val response = connection.inputStream.bufferedReader().use { it.readText() }
        val json = JSONObject(response)

        val status = json.optString("status")
        val routes = json.getJSONArray("routes")

        if (status != "OK" || routes.length() == 0) {
            val errorMsg = json.optString("error_message", "No routes found. Status: $status")
            throw Exception(errorMsg)
        }

        val routeJson = routes.getJSONObject(0)
        val leg = routeJson.getJSONArray("legs").getJSONObject(0)
        
        val steps = leg.getJSONArray("steps")
        val segments = mutableListOf<RouteSegment>()

        for (i in 0 until steps.length()) {
            val step = steps.getJSONObject(i)
            val polyline = step.getJSONObject("polyline").getString("points")
            val points = HelperFunctions.decodePolyline(polyline)
            
            // Note: Standard Directions API might not provide per-step traffic details in standard JSON.
            // However, we check if duration_in_traffic is present at step level or estimate from leg level.
            val duration = step.getJSONObject("duration").getInt("value")
            
            // If the step has specific traffic info (rare in standard API), use it.
            // Otherwise, we can color based on overall leg traffic or keep it LOW.
            val durationInTraffic = step.optJSONObject("duration_in_traffic")?.getInt("value") ?: duration
            
            val ratio = if (duration > 0) durationInTraffic.toFloat() / duration.toFloat() else 1.0f
            
            val level = when {
                ratio > 1.5f -> TrafficLevel.HIGH
                ratio > 1.2f -> TrafficLevel.MEDIUM
                else -> TrafficLevel.LOW
            }
            
            segments.add(RouteSegment(points, level))
        }

        val distance = leg.getJSONObject("distance").getString("text")
        
        // Use duration_in_traffic if available for the summary
        val duration = if (leg.has("duration_in_traffic")) {
            leg.getJSONObject("duration_in_traffic").getString("text")
        } else {
            leg.getJSONObject("duration").getString("text")
        }

        RouteInfo(
            segments = segments,
            distanceText = distance,
            durationText = duration
        )
    }
}
