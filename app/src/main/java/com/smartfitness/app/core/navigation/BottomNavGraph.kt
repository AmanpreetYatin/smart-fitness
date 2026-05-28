package com.smartfitness.app.core.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.smartfitness.app.core.Constants
import com.smartfitness.app.ui.coach.CoachScreen
import com.smartfitness.app.ui.diet.DietDetailScreen
import com.smartfitness.app.ui.diet.DietScreen
import com.smartfitness.app.ui.healthconnect.HealthSyncScreen
import com.smartfitness.app.ui.home.screen.HomeDashboardScreen
import com.smartfitness.app.ui.profile.ProfileScreen
import com.smartfitness.app.ui.tracking.ui.TrackingScreen
import com.smartfitness.app.ui.water.WaterIntakeScreen
import com.smartfitness.app.ui.steps.StepsScreen
import com.smartfitness.app.ui.workout.WorkoutScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomNavGraph(
     navHostController: NavHostController,
     rootController: NavHostController
){

    NavHost(
        navController = navHostController,
        startDestination = ScreenRoute.HomeScreen.route
    ) {
        composable(ScreenRoute.HomeScreen.route) {
            HomeDashboardScreen(
                onWaterClick = { navHostController.navigate(ScreenRoute.WaterIntakeScreen.route) },
                onStepsClick = { navHostController.navigate(ScreenRoute.StepsScreen.route) },
                onHealthSync = {
                    navHostController.navigate(ScreenRoute.HealthSync.route)
                }
            )
        }
        composable(ScreenRoute.ActivityScreen.route) {
            TrackingScreen()
        }
        composable(ScreenRoute.HealthSync.route) {
            HealthSyncScreen(
                navController = navHostController
            )
        }
        composable(ScreenRoute.AddScreen.route) { WorkoutScreen(
            onStartWorkout = {
                rootController.navigate(ScreenRoute.WorkoutDetailScreen.route) // Example: navigate to workout detail with ID 1})
            }
        ) }
        composable(ScreenRoute.DietScreen.route) { DietScreen(onClick = { diet ->
            navHostController.navigate(ScreenRoute.DietDetailScreen.createRoute(diet.id))
        }) }
        composable(ScreenRoute.WaterIntakeScreen.route) {
            WaterIntakeScreen(onBack = { navHostController.popBackStack() })
        }
        composable(ScreenRoute.StepsScreen.route) {
            StepsScreen(onBack = { navHostController.popBackStack() })
        }
        composable(ScreenRoute.ProfileScreen.route) { ProfileScreen(
            rootController = rootController
        ) }

        composable(ScreenRoute.CoachScreen.route) {
            CoachScreen(
                onBack = { navHostController.popBackStack() }
            )
        }
        composable(
            route = ScreenRoute.DietDetailScreen.route,
            arguments = listOf(navArgument("dietId") { type = NavType.IntType })
        ) { backStackEntry ->
            val dietId = backStackEntry.arguments?.getInt("dietId")
            val diet = Constants.sampleDietList.find { it.id == dietId }
            diet?.let { DietDetailScreen(diet = it, navController = navHostController) }
        }

    }

}