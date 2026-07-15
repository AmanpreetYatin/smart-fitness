package com.smartfitness.app.ui.tracking.model

import com.google.android.gms.maps.model.LatLng

data class RouteInfo(
    val segments: List<RouteSegment>,
    val distanceText: String,
    val durationText: String
)

data class RouteSegment(
    val points: List<LatLng>,
    val trafficLevel: TrafficLevel = TrafficLevel.UNKNOWN
)

enum class TrafficLevel {
    LOW,      // Green
    MEDIUM,   // Yellow
    HIGH,     // Red
    UNKNOWN   // Default Blue
}
