package com.smartfitness.app.ui.auth.screens.signin
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.smartfitness.app.R
import com.smartfitness.app.core.snackbar.SnackbarController
import com.smartfitness.app.core.theme.AppBackground
import com.smartfitness.app.core.theme.PrimaryBlue
import com.smartfitness.app.core.utilities.AppLog
import com.smartfitness.app.core.utilities.DialogUtils
import com.smartfitness.app.core.utilities.FacebookAuthHelper
import com.smartfitness.app.ui.auth.viewmodel.AuthViewModel
import com.smartfitness.app.ui.components.AppLoader
import com.smartfitness.app.ui.components.FullScreenLoader
import com.smartfitness.app.ui.components.LottieLoader
import com.smartfitness.app.ui.components.PrimaryButton
import com.smartfitness.app.ui.components.SocialButton
import com.smartfitness.app.ui.components.SoftTextField

@Composable
fun LoginScreen(viewModel: AuthViewModel = hiltViewModel(),
                callbackManager: CallbackManager,
                onLoginSuccess: () -> Unit = {},
                onSignUpClick: () -> Unit = {}
                ) {

    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken!!

                viewModel.signInWithGoogle(idToken)

            } catch (e: Exception) {
                AppLog.v(message = "Google Sign-In Failed: ${e.localizedMessage}")

                Toast.makeText(context, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /*LaunchedEffect(state.toString()) {
        if (state.user != null) {
            Toast.makeText(context, "Login Successful ${state.user?.displayName}", Toast.LENGTH_SHORT).show()
            onLoginSuccess()
        // Navigate to the next screen or perform other actions
        } else if (state.common.error != null) {
            Toast.makeText(context, "Login Failed: ${state.common.error}", Toast.LENGTH_SHORT).show()
        }
    }*/

    LaunchedEffect(Unit) {
        viewModel.event.collect { message ->
            SnackbarController.showError(message = message)
        }
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(AppBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            LoginHeader()
            Spacer(modifier = Modifier.height(32.dp))
            Text("", fontSize = 24.sp)

            Spacer(modifier = Modifier.height(16.dp))
            SoftTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                placeholder = "Email",
                keyboardType = KeyboardType.Email
            )
            Spacer(modifier = Modifier.height(16.dp))

            SoftTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                placeholder = "Password",
                keyboardType = KeyboardType.Password
            )


            Spacer(modifier = Modifier.height(30.dp))

            PrimaryButton(
                text = "Login",
                onClick = { viewModel.login() }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { onLoginSuccess()}) {
                    Text(
                        text = "Forgot Password?",
                        color = PrimaryBlue,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            SocialButton(
                text = "Continue with Google",
                onClick = {
                       viewModel.logout()
                    launcher.launch(viewModel.getGoogleSignInIntent())

                          },
                icon = painterResource(R.drawable.google),
                backgroundColor = Color.White,
                contentColor = Color.Black,
                borderColor = Color(0xFFE0E0E0)
            )
            Spacer(modifier = Modifier.height(16.dp))

            FacebookLoginButton(
                viewModel = viewModel,
                callbackManager = callbackManager,
                activity =  context as Activity,
                onSuccess = { accessToken ->
                    DialogUtils.showToast(context, "Facebook Login Success")
                    onLoginSuccess()
                    AppLog.v(message = "Facebook Access Token: ${accessToken.token}")
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Not registered? ",
                color = Color.Gray
            )

            TextButton(onClick = {
                onSignUpClick()
            },  contentPadding = PaddingValues(0.dp) ) {
                Text(
                    text = "Sign up",
                    color = PrimaryBlue,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (state.common.isLoading){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)), // 🔥 overlay
                contentAlignment = Alignment.Center
            ) {
                /*AppLoader(
                modifier = Modifier.align(Alignment.Center)
                )*/
              /*  CircularProgressIndicator(
                    color = PrimaryBlue
                )*/
                FullScreenLoader()
            }


        }
    }
}


@Composable
fun FacebookLoginButton(
    viewModel: AuthViewModel,
    activity: Activity,
    callbackManager: CallbackManager,
    onSuccess: (AccessToken) -> Unit
) {
    val facebookHelper = remember {
        FacebookAuthHelper(activity, callbackManager)
    }

     SocialButton(
               text = "Continue with Facebook",
               onClick = {
                   facebookHelper.login(
                       onSuccess = {
                            DialogUtils.showToast(activity, "Facebook Login Success")
                            AppLog.v(message = "Facebook Access Token: ${it.token}")
                           viewModel.loginWithFacebook()

                       },
                       onError = {
                           DialogUtils.showToast(activity, "Facebook Login Failed: $it")
                       }
                   )
                         },
                icon = painterResource(R.drawable.facebook),
                backgroundColor = Color(0xFF1877F2),
                contentColor = Color.White,
                isFacebook = true
            )
}