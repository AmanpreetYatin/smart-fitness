package com.smartfitness.app.ui.tracking.model

import com.google.android.gms.maps.model.LatLng

data class RouteInfo(
    val points: List<LatLng>,
    val distanceText: String,
    val durationText: String
)