package com.smartfitness.app.core.utilities

import android.app.Activity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

class FacebookAuthHelper(
    private val activity: Activity,
    private val callbackManager: CallbackManager
) {
    fun login(
        onSuccess: (AccessToken) -> Unit,
        onError: (String) -> Unit
    ) {
        val loginManager = LoginManager.getInstance()

        loginManager.logInWithReadPermissions(
            activity,
            listOf("public_profile", "email")
        )

        loginManager.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {

                override fun onSuccess(result: LoginResult) {
                    onSuccess(result.accessToken)
                }

                override fun onCancel() {}

                override fun onError(error: FacebookException) {
                    onError(error.message ?: "Unknown error")
                }
            }
        )
    }

}