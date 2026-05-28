package com.smartfitness.app.ui.auth.screens.signup
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartfitness.app.ui.auth.viewmodel.AuthViewModel
import com.smartfitness.app.ui.components.AppTextField
import com.smartfitness.app.ui.components.ClickableTextField
import com.smartfitness.app.ui.components.DatePickerView
import com.smartfitness.app.ui.components.HeightWeightPicker
import com.smartfitness.app.ui.components.NumberPickerBottomSheet
import com.smartfitness.app.ui.components.PrimaryButton
import com.smartfitness.app.ui.components.SoftTextField

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SignupScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit = {}
) {

    var showDobPicker by rememberSaveable { mutableStateOf(false) }
    var showWeightPicker by rememberSaveable { mutableStateOf(false) }
    var showHeightPicker by rememberSaveable { mutableStateOf(false) }

    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FBFF))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .imePadding()
            .navigationBarsPadding()
    ) {

        Spacer(modifier = Modifier.height(80.dp))

        // 🔙 Back button + Title row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Create Account",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Start your fitness journey",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 48.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        // 🔹 Inputs
        SoftTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            placeholder = "Email Address",
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(14.dp))

        SoftTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            placeholder = "Password",
            isPassword = true,
            keyboardType = KeyboardType.Password
        )

        Spacer(modifier = Modifier.height(14.dp))

        SoftTextField(
            value = state.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            placeholder = "Confirm Password",
            isPassword = true,
            keyboardType = KeyboardType.Password
        )

        Spacer(modifier = Modifier.height(28.dp))

        // 🔸 Section Label
        Text(
            text = "Optional Info",
            fontSize = 13.sp,
            color = Color.Gray,
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 🔹 DOB
      /*  SoftTextField(
            value = state.dob,
            onValueChange = viewModel::onDobChange,
            placeholder = "Date of Birth",
            isFocusable = false,
            onClick = { showDobPicker = true }
        )*/
        ClickableTextField(
            value =  state.dob,
            placeholder = "Date of Birth",
            onClick = { showDobPicker = true },
            isActive = showDobPicker
        )


        Spacer(modifier = Modifier.height(14.dp))

        // 🔹 Weight & Height
        Row {
            /*SoftTextField(
                value = state.weight,
                onValueChange = viewModel::onWeightChange,
                placeholder = "Weight (kg)",
                modifier = Modifier.weight(1f),
                keyboardType = KeyboardType.Number
            )*/

            ClickableTextField(
                value =  state.weight,
                placeholder = "Weight (kg)",
                modifier = Modifier.weight(1f),
                onClick = { showWeightPicker = true },
                isActive = showWeightPicker
            )

            Spacer(modifier = Modifier.width(12.dp))

            /*SoftTextField(
                value = state.height,
                onValueChange = viewModel::onHeightChange,
                placeholder = "Height (cm)",
                modifier = Modifier.weight(1f),
                keyboardType = KeyboardType.Number
            )*/

            ClickableTextField(
                value =  state.height,
                placeholder = "Height (cm)",
                modifier = Modifier.weight(1f),
                onClick = { showHeightPicker = true },
                isActive = showHeightPicker
            )

        }

        Spacer(modifier = Modifier.height(36.dp))

        // 🔵 CTA Button (Centered feel)
        PrimaryButton(
            text = "Create Account",
            onClick = { },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 🔻 Bottom Text
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Already have an account? ",
                color = Color.Gray
            )

            Text(
                text = "Login",
                color = Color(0xFF113DFA),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onLoginClick() }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    if (showDobPicker) {
        DatePickerView(
            onDateSelected = {
                viewModel.onDobChange(it)
                showDobPicker = false
            },
            onDismiss = { showDobPicker = false }
        )
    }
    if (showWeightPicker) {
        HeightWeightPicker(
            title = "Select Weight",
            unit = "Kg",
            range = 60..220,
            onSelect = {
                viewModel.onWeightChange(it.toString())
            },
            onDismiss = { showWeightPicker = false }
        )
    }
    if (showHeightPicker) {
        HeightWeightPicker(
            title = "Select Height",
            unit = "cm",
            range = 100..220,
            onSelect = {
                viewModel.onHeightChange(it.toString())
            },
            onDismiss = { showHeightPicker = false }
        )
    }
}