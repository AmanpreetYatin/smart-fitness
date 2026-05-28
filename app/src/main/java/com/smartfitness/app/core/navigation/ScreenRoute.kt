package com.smartfitness.app.core.navigation

sealed class ScreenRoute(val route: String) {
    object IntroScreen: ScreenRoute("intro_screen")
    object LoginScreen: ScreenRoute("login_screen")
    object SignupScreen: ScreenRoute("signup_screen")
    object HomeScreen: ScreenRoute("home_screen")
    object ActivityScreen: ScreenRoute("activity_screen")
    object AddScreen: ScreenRoute("add_screen")
    object DietScreen: ScreenRoute("diet_screen")
    object CoachScreen: ScreenRoute("coach_screen")
    object DietDetailScreen: ScreenRoute("diet_detail_screen/{dietId}") {
        fun createRoute(dietId: Int) = "diet_detail_screen/$dietId"
    }
    object ProfileScreen: ScreenRoute("profile_screen")
    object ChatScreen: ScreenRoute("chat_screen")
    object WorkoutDetailScreen: ScreenRoute("workout_detail_screen")
    object WaterIntakeScreen: ScreenRoute("water_intake_screen")
    object StepsScreen: ScreenRoute("steps_screen")
    object HealthSync: ScreenRoute("health_sync")
    object ChangePasswordScreen: ScreenRoute("change_password_screen")
}