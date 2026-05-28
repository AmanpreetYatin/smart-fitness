package com.smartfitness.app.ui.tracking.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.maps.android.compose.Polyline
import com.smartfitness.app.R
import com.smartfitness.app.core.utilities.HelperFunctions.bitmapDescriptorFromVector

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.smartfitness.app.core.utilities.HelperFunctions.resizedMarkerIcon
import com.smartfitness.app.ui.tracking.service.LocationTrackingService

@Composable
fun TrackingScreen(viewModel: TrackingViewModel = hiltViewModel()) {

    val animatedLocation by viewModel.animatedLatLng.collectAsState()
    val route by viewModel.route.collectAsState()
    val bearing by viewModel.bearing.collectAsState()
    val eta by viewModel.eta.collectAsState()
    val distance by viewModel.distance.collectAsState()
    val cameraPositionState = rememberCameraPositionState()
    val context = LocalContext.current
    val riderIcon = remember { mutableStateOf<BitmapDescriptor?>(null) }
    val lifecycleOwner = LocalLifecycleOwner.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val locationGranted = results[Manifest.permission.ACCESS_FINE_LOCATION] == true
        if (locationGranted) {
            viewModel.startTracking()
            ContextCompat.startForegroundService(
                context,
                Intent(context, LocationTrackingService::class.java)
            )
        }
    }

    LaunchedEffect(Unit) {
        val locationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // Collect any permissions that still need to be requested
        val permissionsToRequest = buildList {
            if (!locationGranted) {
                add(Manifest.permission.ACCESS_FINE_LOCATION)
                add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val notifGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                if (!notifGranted) add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }.toTypedArray()

        if (locationGranted) {
            // Location already granted — start tracking immediately
            viewModel.startTracking()
            ContextCompat.startForegroundService(
                context,
                Intent(context, LocationTrackingService::class.java)
            )
            // Still ask for any other missing permissions (e.g. notifications)
            if (permissionsToRequest.isNotEmpty()) {
                permissionLauncher.launch(permissionsToRequest)
            }
        } else {
            // Location not granted — ask for everything
            permissionLauncher.launch(permissionsToRequest)
        }
    }

    DisposableEffect(lifecycleOwner) {

        onDispose {
            context.stopService(
                Intent(context, LocationTrackingService::class.java)
            )
        }
    }

    LaunchedEffect(Unit) {
        riderIcon.value =
            resizedMarkerIcon(
                context,
                R.drawable.ic_rider,
                width = 80,
                height = 80
            )


           // bitmapDescriptorFromVector(context, R.drawable.ic_rider)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {

            // ✅ 1. Rider Marker (ONLY ONE)
            animatedLocation?.let { latLng ->

                Marker(
                    state = MarkerState(position = latLng),
                    title = "Rider",
                    icon = riderIcon.value,
                    rotation = bearing,
                    anchor = androidx.compose.ui.geometry.Offset(0.5f, 1f),
                    flat = true
                )

                // ✅ Camera follow
                LaunchedEffect(latLng) {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(latLng, 17f)
                    )
                }
            }

            // ✅ 2. Route Polyline
            if (route.isNotEmpty()) {
                Polyline(
                    points = route,
                    width = 8f,
                    color = Color.Blue
                )
            }

        }
        EtaCard(
            eta = eta,
            distance = distance,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}