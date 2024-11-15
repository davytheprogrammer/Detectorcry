package com.androidlead.loginappui.ui.screen.container

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.androidlead.loginappui.ui.screen.chat.ChatScreen
import com.androidlead.loginappui.ui.screen.doctor.Doctor
import com.androidlead.loginappui.ui.screen.doctor.DoctorScreen
import com.androidlead.loginappui.ui.screen.home.EnhancedDashboard
import com.androidlead.loginappui.ui.screen.login.LoginScreen
import com.androidlead.loginappui.ui.screen.registration.RegistrationScreen
import com.androidlead.loginappui.ui.screen.welcome.WelcomeScreen

@Composable
fun ScreenContainer() {
    val navHost = rememberNavController()

    NavHost(
        navController = navHost,
        startDestination = NavGraph.Welcome.route
    ) {
        composable(NavGraph.Welcome.route) {
            WelcomeScreen(
                onOpenLoginClicked = {
                    navHost.navigate(NavGraph.Login.route)
                }
            )
        }

        composable(NavGraph.Login.route) {
            LoginScreen(
                onLoginClicked = {
                    navHost.navigate(NavGraph.Home.route) {
                        // Clear the back stack up to Home
                        popUpTo(NavGraph.Welcome.route) { inclusive = true }
                    }
                },
                onRegistrationClicked = {
                    navHost.navigate(NavGraph.Registration.route)
                }
            )
        }

        composable(NavGraph.Registration.route) {
            RegistrationScreen(
                onRegisterClicked = {
                    navHost.navigate(NavGraph.Home.route) {
                        // Clear the back stack up to Home
                        popUpTo(NavGraph.Welcome.route) { inclusive = true }
                    }
                },
                onLoginClicked = {
                    navHost.navigate(NavGraph.Login.route)
                }
            )
        }

        composable(NavGraph.Home.route) {
            EnhancedDashboard(
                onDoctorClick = {
                    navHost.navigate(NavGraph.Doctorer.route)
                },
                onChatClick = {
                    navHost.navigate(NavGraph.Chat.route)
                }
            )
        }

        composable(NavGraph.Doctorer.route) {
            DoctorScreen(

            )
        }

        composable(NavGraph.Chat.route) {
            ChatScreen(

            )
        }
    }
}