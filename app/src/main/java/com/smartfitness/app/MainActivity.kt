package com.smartfitness.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.facebook.CallbackManager
import com.smartfitness.app.core.navigation.RootNavGraph
import com.smartfitness.app.core.snackbar.GlobalSnackbarHost
import com.smartfitness.app.core.theme.SmartFitnessTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        callbackManager = CallbackManager.Factory.create()
        setContent {
            SmartFitnessTheme {
                val navController   = rememberNavController()
                val snackbarState   = remember { SnackbarHostState() }

                Box(modifier = Modifier.fillMaxSize()) {
                    RootNavGraph(
                        callbackManager = callbackManager,
                        navHostController = navController,
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    GlobalSnackbarHost(hostState = snackbarState)
                }
            }
        }
    }
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}