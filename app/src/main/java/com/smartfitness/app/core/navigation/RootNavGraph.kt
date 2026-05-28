package com.smartfitness.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.facebook.CallbackManager
import com.smartfitness.app.ui.auth.screens.signin.LoginScreen
import com.smartfitness.app.ui.auth.screens.signup.SignupScreen
import com.smartfitness.app.ui.chat.ChatScreen
import com.smartfitness.app.ui.home.screen.MainScreen
import com.smartfitness.app.ui.intro.IntroScreen
import com.smartfitness.app.ui.profile.ChangePasswordScreen
import com.smartfitness.app.ui.workoutdetail.WorkoutDetailScreen

@Composable
fun RootNavGraph(
    callbackManager: CallbackManager,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
){

    NavHost(
        navController = navHostController,
        startDestination = ScreenRoute.IntroScreen.route
    ) {
        composable(ScreenRoute.IntroScreen.route){
            IntroScreen(){
                navHostController.navigate(ScreenRoute.LoginScreen.route)
            }
        }
        composable(ScreenRoute.LoginScreen.route){
            LoginScreen(
                callbackManager = callbackManager,
                onLoginSuccess = {
                    navHostController.navigate(ScreenRoute.HomeScreen.route){
                        popUpTo(ScreenRoute.LoginScreen.route){
                            inclusive = true
                        }
                    }
                },
                onSignUpClick = {
                    navHostController.navigate(ScreenRoute.SignupScreen.route)
                }
            )
        }
        composable(ScreenRoute.HomeScreen.route){
            MainScreen(rootNavController =  navHostController)
        }
        composable(ScreenRoute.ChatScreen.route){
            ChatScreen(navHostController = navHostController)
        }

        composable(ScreenRoute.SignupScreen.route){
            SignupScreen(
                onLoginClick = { navHostController.popBackStack() },
                onBackClick = { navHostController.popBackStack() }
            )
        }
        composable(ScreenRoute.WorkoutDetailScreen.route){
            WorkoutDetailScreen (
                onWorkoutComplete = {
                    navHostController.popBackStack()
                },
                onBack = {
                    navHostController.popBackStack()
                }
            )
        }
        composable(ScreenRoute.ChangePasswordScreen.route){
            ChangePasswordScreen(navController = navHostController)
        }
    }

}