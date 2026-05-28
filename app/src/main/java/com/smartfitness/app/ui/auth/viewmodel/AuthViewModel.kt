package com.smartfitness.app.ui.auth.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.smartfitness.app.core.network.NetworkResult
import com.smartfitness.app.core.utilities.AppValidator
import com.smartfitness.app.core.utilities.DialogUtils
import com.smartfitness.app.core.utilities.ValidationResult
import com.smartfitness.app.ui.auth.repository.AuthRepository
import com.smartfitness.app.ui.auth.screens.state.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository,
    val googleClient: GoogleSignInClient
) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    private val _event = MutableSharedFlow<String>()
    val event = _event.asSharedFlow()



    fun loginWithFacebook(){

    }
    fun getGoogleSignInIntent(): Intent {
        return googleClient.signInIntent
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
               repo.signInWithGoogle(idToken).collectLatest {  result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(
                                user = result.data,
                                common = it.common.copy(isLoading = false)
                            )
                        }
                    }
                    is NetworkResult.Error ->
                        _uiState.update {
                            it.copy(
                                common = it.common.copy(isLoading = false,error = result.message)
                            )
                        }
                    is NetworkResult.Loading -> {
                         _uiState.update {
                            it.copy(
                                common = it.common.copy(isLoading = true)
                            )
                        }
                    }
                }

            }

        }
    }

    fun logout() {
        googleClient.signOut()
        FirebaseAuth.getInstance().signOut()
    }

    fun login()
    {
        val email = uiState.value.email
        val password = uiState.value.password
        val emailResult = AppValidator.validateEmail(email)
        val passwordResult = AppValidator.validatePassword(password)

        if (emailResult is ValidationResult.Error) {
            viewModelScope.launch {
                _event.emit(emailResult.message)
            }
            return
        }

        if (passwordResult is ValidationResult.Error) {
            viewModelScope.launch {
                _event.emit(passwordResult.message)
            }
            return
        }

        viewModelScope.launch {
            repo.login(email, password).collect {
                when (it) {
                    is NetworkResult.Success -> _uiState.update { state ->
                        state.copy(
                            user = it.data,
                            common = state.common.copy(isLoading = false, isSuccess = true, error = null)
                        )
                    }
                    is NetworkResult.Error -> _uiState.update { state ->
                        state.copy(
                            common = state.common.copy(isLoading = false, error = it.message)
                        )
                    }
                    is NetworkResult.Loading -> _uiState.update { state ->
                        state.copy(
                            common = state.common.copy(isLoading = true)
                        )
                    }
                }
            }
        }
    }

    fun onEmailChange(value: String) {
        _uiState.update {
            it.copy(
                email = value
            )
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                password = value
            )
        }
    }
    fun onConfirmPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                confirmPassword = value
            )
        }
    }

    fun onDobChange(value: String) {  _uiState.update {
        it.copy(
            dob = value
        )
    } }
    fun onWeightChange(value: String) {  _uiState.update {
        it.copy(
            weight = value
        )
    }}
    fun onHeightChange(value: String) {  _uiState.update {
        it.copy(
            height = value
        )
    } }
}