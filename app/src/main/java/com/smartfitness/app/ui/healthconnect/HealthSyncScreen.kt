package com.smartfitness.app.ui.healthconnect

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission.Companion.getReadPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.smartfitness.app.core.ui.components.SmartFitnessTopBar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthSyncScreen(
    navController: NavController,
    vm: HealthViewModel = viewModel()
) {
    val context = LocalContext.current
    val manager = remember { HealthConnectManager(context) }
    val state by vm.state.collectAsState()
    val permissions =
        setOf(
            getReadPermission(StepsRecord::class),
            getReadPermission(DistanceRecord::class),
            getReadPermission(TotalCaloriesBurnedRecord::class),
        )

    val client = remember {
        HealthConnectClient.getOrCreate(context)
    }

    val checkHealthConnectAvailability = remember {
        { onAvailable: () -> Unit ->
            val sdkStatus = HealthConnectClient.getSdkStatus(context)
            if (sdkStatus == HealthConnectClient.SDK_UNAVAILABLE) {
                Toast.makeText(context, "Health Connect is not available on this device", Toast.LENGTH_SHORT).show()
            } else if (sdkStatus == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
                Toast.makeText(context, "Health Connect requires an update from the Play Store", Toast.LENGTH_SHORT).show()
            } else {
                onAvailable()
            }
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = PermissionController.createRequestPermissionResultContract()
        ) { granted ->

            if (granted.containsAll(permissions)) {
                vm.loadSteps(manager)
            }
        }
    LaunchedEffect(Unit) {
        val sdkStatus = HealthConnectClient.getSdkStatus(context)
        if (sdkStatus == HealthConnectClient.SDK_UNAVAILABLE) {
            Toast.makeText(context, "Health Connect is not available on this device", Toast.LENGTH_SHORT).show()
        } else if (sdkStatus == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
            Toast.makeText(context, "Health Connect requires an update from the Play Store", Toast.LENGTH_SHORT).show()
        } else {
            val granted = client.permissionController.getGrantedPermissions()
            if (granted.containsAll(permissions)) {
                vm.loadSteps(manager)
            }
        }
    }

    Scaffold(
        topBar = {
            SmartFitnessTopBar(
                title = "Health Sync",
                showBackButton = true,
                onBackClick = {
                    navController.popBackStack()
                }
            )

        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    checkHealthConnectAvailability {
                        permissionLauncher.launch(permissions)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Connect Health Data")
            }

            Button(
                onClick = {
                    checkHealthConnectAvailability {
                        permissionLauncher.launch(permissions)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sync Now")
            }

            if (state.loading) {
                CircularProgressIndicator()
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier.padding(16.dp)
                ) {
                    Text("Today's Steps")
                    Text(
                        text = "${state.steps}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }
    }
}