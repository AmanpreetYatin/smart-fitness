package com.smartfitness.app.ui.home.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartfitness.app.core.navigation.BottomNavGraph
import com.smartfitness.app.core.navigation.ScreenRoute
import com.smartfitness.app.core.theme.Background
import com.smartfitness.app.core.theme.BrandColor

@Composable
fun MainScreen(rootNavController : NavHostController) {
    // This controller drives the bottom nav tabs — separate from the root nav controller
    val bottomNavController = rememberNavController()

    Scaffold(
        containerColor = Background,
        modifier = Modifier.navigationBarsPadding(),
        bottomBar = {
            BottomBar(navHostController = bottomNavController)
        },
       /* floatingActionButton = {
            FloatingActionButton(
                onClick = { bottomNavController.navigate(ScreenRoute.AddScreen.route) },
                containerColor = BrandColor,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            }
        },
        floatingActionButtonPosition = FabPosition.Center*/
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            // Inner NavHost — handles all bottom nav tab screens
            BottomNavGraph(navHostController = bottomNavController, rootController = rootNavController )
        }
    }
}