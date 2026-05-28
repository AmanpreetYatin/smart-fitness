package com.smartfitness.app.core.common


//App Button import

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartfitness.app.core.theme.AppBackground
import com.smartfitness.app.ui.auth.viewmodel.AuthViewModel
import com.smartfitness.app.ui.components.AppButton
import com.smartfitness.app.ui.components.AppTextField
import com.smartfitness.app.ui.components.SocialButton

/////////

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartfitness.app.R
import com.smartfitness.app.core.theme.PrimaryBlue

// Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

// Navigation
import androidx.navigation.NavController
import androidx.navigation.compose.*

// Coroutines
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*