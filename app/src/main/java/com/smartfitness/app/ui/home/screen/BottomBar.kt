package com.smartfitness.app.ui.home.screen
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.smartfitness.app.R
import com.smartfitness.app.core.navigation.ScreenRoute
import com.smartfitness.app.core.theme.AppBackground
import com.smartfitness.app.core.theme.Background
import com.smartfitness.app.core.theme.BrandColor
import com.smartfitness.app.core.theme.CardColor
import com.smartfitness.app.core.theme.PrimaryBlue
import com.smartfitness.app.core.theme.TextSecondary
import com.smartfitness.app.core.utilities.AppLog
import com.smartfitness.app.ui.auth.viewmodel.AuthViewModel
import com.smartfitness.app.ui.components.PrimaryButton
import com.smartfitness.app.ui.components.SocialButton
import com.smartfitness.app.ui.components.SoftTextField


data class BottomNavItem(val label: String, val icon: ImageVector, val route: String)
val navItemList = listOf(
    BottomNavItem("Home", Icons.Default.Home, ScreenRoute.HomeScreen.route),
    BottomNavItem("Activity", Icons.Default.ShowChart, ScreenRoute.ActivityScreen.route),
    BottomNavItem("Add", Icons.Default.Add, ScreenRoute.AddScreen.route),
    BottomNavItem("Diet", Icons.Default.Restaurant, ScreenRoute.DietScreen.route),
    BottomNavItem("Coach", Icons.Default.SportsGymnastics, ScreenRoute.CoachScreen.route),
    BottomNavItem("Profile", Icons.Default.Person, ScreenRoute.ProfileScreen.route)
)
@Composable
fun BottomBar(navHostController: NavHostController) {


    val currentBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = CardColor
    ) {
        navItemList.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                          navHostController.navigate(item.route){
                              launchSingleTop = true
                                popUpTo(navHostController.graph.startDestinationId) {
                                    saveState = true
                                }
                                restoreState = true
                          }
                          },
                icon = {
                    Icon(
                        imageVector =  item.icon,
                        contentDescription = item.label
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BrandColor,
                    unselectedIconColor = TextSecondary
                )
            )
        }
    }
}