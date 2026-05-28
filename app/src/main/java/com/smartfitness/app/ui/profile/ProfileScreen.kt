package com.smartfitness.app.ui.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.smartfitness.app.R
import com.smartfitness.app.core.navigation.ScreenRoute
import com.smartfitness.app.core.theme.BrandColor
import com.smartfitness.app.core.utilities.rememberImagePickerHelper
import com.smartfitness.app.ui.components.ImageSelectionSourceDialog
import com.smartfitness.app.ui.components.LogoutDialog

@Composable
fun ProfileScreen(rootController: NavHostController) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }

    val imagePickerHelper = rememberImagePickerHelper { uri ->
        selectedImageUri = uri
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            ProfileHeader(
                selectedImageUri = selectedImageUri,
                onEditClick = { showImageSourceDialog = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            ProfileOptions(rootController)

        }
    }

    if (showImageSourceDialog) {
        ImageSelectionSourceDialog(
            onDismiss = { showImageSourceDialog = false },
            onGalleryClick = {
                showImageSourceDialog = false
                imagePickerHelper.openGallery()
            },
            onCameraClick = {
                showImageSourceDialog = false
                imagePickerHelper.openCamera()
            }
        )
    }
}

@Composable
fun ProfileHeader(
    selectedImageUri: Uri? = null,
    onEditClick: () -> Unit = {}
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.clickable { onEditClick() }
        ) {
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .border(3.dp, BrandColor, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = null,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .border(3.dp, BrandColor, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            // Edit icon
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(BrandColor, CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Amanpreet Singh",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            "Stay consistent 💪",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ProfileOptions(rootController: NavHostController) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        ProfileOptionItem("Change Password", Icons.Default.Lock, onClick = {
            rootController.navigate(ScreenRoute.ChangePasswordScreen.route)
        })
        ProfileOptionItem("Privacy Policy", Icons.Default.PrivacyTip, onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
            context.startActivity(intent)
        })
        ProfileOptionItem("Chat with AI", Icons.Default.SmartToy , onClick = {
            rootController.navigate(ScreenRoute.ChatScreen.route)
        })
        ProfileOptionItem("Logout", Icons.Default.ExitToApp, isDanger = true , onClick = {
            showDialog = true
        })

        LogoutDialog(
            show = showDialog,
            onDismiss = { showDialog = false },
            onConfirm = {
                rootController.navigate(ScreenRoute.LoginScreen.route){
                    popUpTo(0){inclusive = true}
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun ProfileOptionItem(
    title: String,
    icon: ImageVector,
    isDanger: Boolean = false,
    onClick: () -> Unit = {}
) {

    val textColor = if (isDanger) Color.Red else MaterialTheme.colorScheme.onSurface

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(BrandColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = BrandColor)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                color = textColor,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )

            Icon(
                Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
