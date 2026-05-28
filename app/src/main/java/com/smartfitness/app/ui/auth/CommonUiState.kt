package com.smartfitness.app.ui.auth


data  class CommonUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
