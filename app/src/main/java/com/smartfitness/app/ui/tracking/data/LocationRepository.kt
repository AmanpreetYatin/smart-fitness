package com.smartfitness.app.ui.tracking.data

import com.smartfitness.app.core.utilities.HelperFunctions
import com.smartfitness.app.ui.tracking.model.RiderLocation
import com.smartfitness.app.ui.tracking.model.RouteInfo
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

    suspend fun fetchRoute(origin: String, dest: String): RouteInfo = withContext(Dispatchers.IO) {

        val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=$origin&destination=$dest&key="

        val response = URL(url).readText()
        val json = JSONObject(response)

        val status = json.optString("status")
        val routes = json.getJSONArray("routes")

        if (status != "OK" || routes.length() == 0) {
            val errorMsg = json.optString("error_message", "No routes found. Status: $status")
            throw Exception(errorMsg)
        }

        val route = routes.getJSONObject(0)

        val leg = route
            .getJSONArray("legs")
            .getJSONObject(0)

        val distance = leg
            .getJSONObject("distance")
            .getString("text")

        val duration = leg
            .getJSONObject("duration")
            .getString("text")

        val polyline = route
            .getJSONObject("overview_polyline")
            .getString("points")

        RouteInfo(
            points = HelperFunctions.decodePolyline(polyline),
            distanceText = distance,
            durationText = duration
        )
    }
}
