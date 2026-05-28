package com.smartfitness.app.core.utilities

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImagePickerHelper(
    private val context: Context,
    private val cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    private val galleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
    private val permissionHelper: PermissionHelper
) {
    var tempImageUri by mutableStateOf<Uri?>(null)
        private set

    fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    fun openCamera() {
        permissionHelper.requestPermission(Manifest.permission.CAMERA) { isGranted ->
            if (isGranted) {
                launchCameraInternal()
            } else {
                Toast.makeText(context, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun launchCameraInternal() {
        val uri = createImageUri()
        if (uri != null) {
            tempImageUri = uri
            cameraLauncher.launch(uri)
        }
    }

    private fun createImageUri(): Uri? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return try {
            val file = File.createTempFile(
                "IMG_${timeStamp}_",
                ".jpg",
                storageDir
            )
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

@Composable
fun rememberImagePickerHelper(onImageSelected: (Uri) -> Unit): ImagePickerHelper {
    val context = LocalContext.current
    val permissionHelper = rememberPermissionHelper()
    
    val helperState = remember { mutableStateOf<ImagePickerHelper?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            helperState.value?.tempImageUri?.let { onImageSelected(it) }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }

    return remember(cameraLauncher, galleryLauncher, permissionHelper) {
        ImagePickerHelper(context, cameraLauncher, galleryLauncher, permissionHelper).also {
            helperState.value = it
        }
    }
}
