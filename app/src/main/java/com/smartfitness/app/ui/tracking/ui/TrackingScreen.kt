package com.smartfitness.app.ui.tracking.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.LatLngBounds
import com.smartfitness.app.R
import com.smartfitness.app.core.theme.BrandColor
import com.smartfitness.app.ui.tracking.model.TrafficLevel
import com.smartfitness.app.core.utilities.HelperFunctions.resizedMarkerIcon
import com.smartfitness.app.ui.tracking.service.LocationTrackingService

@Composable
fun TrackingScreen(viewModel: TrackingViewModel = hiltViewModel()) {

    val animatedLocation by viewModel.animatedLatLng.collectAsState()
    val route by viewModel.route.collectAsState()
    val bearing by viewModel.bearing.collectAsState()
    val eta by viewModel.eta.collectAsState()
    val distance by viewModel.distance.collectAsState()
    val destination by viewModel.destination.collectAsState()
    val isTrackingStarted by viewModel.isTrackingStarted.collectAsState()
    val cameraPositionState = rememberCameraPositionState()
    val context = LocalContext.current
    val riderIcon = remember { mutableStateOf<BitmapDescriptor?>(null) }
    val lifecycleOwner = LocalLifecycleOwner.current

    var showDialog by remember { mutableStateOf(false) }
    var destinationAddress by remember { mutableStateOf("") }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Set Destination",
                        style = MaterialTheme.typography.titleLarge,
                        color = BrandColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = destinationAddress,
                        onValueChange = { destinationAddress = it },
                        label = { Text("Enter Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            if (destinationAddress.isNotBlank()) {
                                viewModel.setDestination(destinationAddress, context)
                                showDialog = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val locationGranted = results[Manifest.permission.ACCESS_FINE_LOCATION] == true
        if (locationGranted) {
            // Permission granted — user can now start tracking by clicking the button
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
            // Location already granted
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
            cameraPositionState = cameraPositionState,
            properties = com.google.maps.android.compose.MapProperties(isTrafficEnabled = true)
        ) {

            // ✅ 1. Rider Marker (ONLY ONE)
            animatedLocation?.let { latLng ->

                Marker(
                    state = MarkerState(position = latLng),
                    title = "Rider",
                    icon = riderIcon.value,
                    rotation = bearing,
                    anchor = androidx.compose.ui.geometry.Offset(0.5f, 0.5f),
                    flat = true
                )

                // ✅ Camera follow - Zoom to fit the entire route
                LaunchedEffect(latLng, route) {
                    if (route.isNotEmpty()) {
                        val bounds = LatLngBounds.builder().apply {
                            route.forEach { segment ->
                                segment.points.forEach { include(it) }
                            }
                            include(latLng) // Ensure current rider location is included
                        }.build()
                        
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngBounds(bounds, 150),
                            1000
                        )
                    } else {
                        // If no route yet, just follow the rider
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(latLng, 17f)
                        )
                    }
                }
            }

            // ✅ 2. Destination Marker
            destination?.let { dest ->
                Marker(
                    state = MarkerState(position = dest),
                    title = "Destination",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                )
            }

            // ✅ 3. Route Polyline with Traffic Colors
            route.forEach { segment ->
                val polylineColor = when (segment.trafficLevel) {
                    TrafficLevel.HIGH -> Color(0xFFF44336)   // Red for High Traffic
                    else -> Color.Blue                       // Blue for everything else
                }

                Polyline(
                    points = segment.points,
                    width = 18f,
                    color = polylineColor
                )
            }

        }

        // Buttons at Top
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Set Destination")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.startTracking() },
                enabled = destination != null && !isTrackingStarted,
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text(if (isTrackingStarted) "Tracking Active" else "Start Tracking")
            }
        }

        EtaCard(
            eta = eta,
            distance = distance,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
