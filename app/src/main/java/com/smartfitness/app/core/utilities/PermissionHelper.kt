package com.smartfitness.app.core.utilities

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext

class PermissionHelper(
    private val context: Context,
    private val singlePermissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    private val multiplePermissionsLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>
) {
    private var onSinglePermissionResult: ((Boolean) -> Unit)? = null
    private var onMultiplePermissionsResult: ((Map<String, Boolean>) -> Unit)? = null

    /**
     * Checks if a specific permission is granted.
     */
    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Requests a single permission and returns the result via callback.
     * If the permission is already granted, the callback is triggered immediately with true.
     */
    fun requestPermission(permission: String, onResult: (Boolean) -> Unit) {
        if (isPermissionGranted(permission)) {
            onResult(true)
        } else {
            onSinglePermissionResult = onResult
            singlePermissionLauncher.launch(permission)
        }
    }

    /**
     * Requests multiple permissions and returns the results via callback.
     */
    fun requestMultiplePermissions(permissions: Array<String>, onResult: (Map<String, Boolean>) -> Unit) {
        onMultiplePermissionsResult = onResult
        multiplePermissionsLauncher.launch(permissions)
    }

    // Internal methods used by the composable launcher wrapper
    internal fun handleSinglePermissionResult(isGranted: Boolean) {
        onSinglePermissionResult?.invoke(isGranted)
        onSinglePermissionResult = null
    }

    internal fun handleMultiplePermissionsResult(result: Map<String, Boolean>) {
        onMultiplePermissionsResult?.invoke(result)
        onMultiplePermissionsResult = null
    }
}

/**
 * Creates and remembers a PermissionHelper to be used within a Composable.
 */
@Composable
fun rememberPermissionHelper(): PermissionHelper {
    val context = LocalContext.current
    
    // We use a state to hold the helper so the launchers can refer to it
    val helperState = remember { mutableStateOf<PermissionHelper?>(null) }

    val singleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        helperState.value?.handleSinglePermissionResult(isGranted)
    }

    val multipleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        helperState.value?.handleMultiplePermissionsResult(result)
    }

    return remember(singleLauncher, multipleLauncher) {
        PermissionHelper(context, singleLauncher, multipleLauncher).also {
            helperState.value = it
        }
    }
}
