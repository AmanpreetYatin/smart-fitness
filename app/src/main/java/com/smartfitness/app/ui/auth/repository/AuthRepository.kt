package com.smartfitness.app.ui.auth.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.smartfitness.app.core.network.NetworkResult
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class AuthRepository  @Inject constructor(
    private val auth: FirebaseAuth
) {
    fun login(email: String, password: String): Flow<NetworkResult<FirebaseUser?>> = flow {
        emit(NetworkResult.Loading)
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            emit(NetworkResult.Success(result.user))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Error"))
        }
    }

    fun signup(email: String, password: String): Flow<NetworkResult<FirebaseUser?>> = flow {
        emit(NetworkResult.Loading)
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            emit(NetworkResult.Success(result.user))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Error"))
        }
    }

    suspend fun signInWithGoogle(idToken: String): Flow<NetworkResult<FirebaseUser?>> = flow {
        emit(NetworkResult.Loading)
         try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            emit(NetworkResult.Success(result.user))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Google Sign-In Failed"))
        }
    }.flowOn(Dispatchers.IO)

}