package com.smartfitness.app.ui.auth.screens.state

import com.google.firebase.auth.FirebaseUser
import com.smartfitness.app.ui.auth.CommonUiState

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    val dob: String = "",
    val weight: String = "",
    val height: String = "",

    val user: FirebaseUser? =null,
    val common: CommonUiState = CommonUiState()
)