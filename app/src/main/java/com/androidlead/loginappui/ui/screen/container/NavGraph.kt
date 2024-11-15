package com.androidlead.loginappui.ui.screen.container

sealed class NavGraph(val route: String) {
    data object Welcome: NavGraph(route = "welcome_screen")
    data object Login: NavGraph(route = "login_screen")
    data object Registration: NavGraph(route = "registration_screen")
    data object Home: NavGraph(route = "home_screen")
    data object Doctorer: NavGraph(route = "doctor_screen")
    data object Chat: NavGraph(route = "chat_screen")
    data object Profile: NavGraph(route = "profile_screen")
}