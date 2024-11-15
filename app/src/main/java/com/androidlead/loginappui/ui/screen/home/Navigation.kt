package com.androidlead.loginappui.ui.components

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import com.androidlead.loginappui.R
import com.androidlead.loginappui.ui.theme.PrimaryGreen
import com.androidlead.loginappui.ui.theme.PrimaryViolet

@Composable
fun BottomNavBar(
    selectedRoute: String,
    onTabSelected: (String) -> Unit
) {
    val navBarItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = PrimaryGreen,
        unselectedIconColor = Color.White,
        selectedTextColor = PrimaryGreen,
        unselectedTextColor = Color.White,
        indicatorColor = Color.White
    )

    NavigationBar(
        containerColor = PrimaryViolet,
        contentColor = Color.White
    ) {
        // Doctor Tab
        NavigationBarItem(
            selected = selectedRoute == "doctor_screen",
            onClick = { onTabSelected("doctor_screen") },
            icon = { Icon(Icons.Default.Person, contentDescription = "Doctor") },
            label = { Text("Doctor") },
            colors = navBarItemColors
        )

        // Chat Tab
        NavigationBarItem(
            selected = selectedRoute == "chat_screen",
            onClick = { onTabSelected("chat_screen") },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.chat),
                    contentDescription = "AI Chat",
                    tint = if (selectedRoute == "chat_screen") PrimaryGreen else Color.White
                )
            },
            label = { Text("AI Chat") },
            colors = navBarItemColors
        )

        // Profile Tab
        NavigationBarItem(
            selected = selectedRoute == "profile_screen",
            onClick = { onTabSelected("profile_screen") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Profile") },
            label = { Text("Profile") },
            colors = navBarItemColors
        )
    }
}
