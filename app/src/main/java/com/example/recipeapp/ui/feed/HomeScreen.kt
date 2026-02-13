package com.example.recipeapp.ui.feed

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToRecipeDetails: (Int) -> Unit,
    onNavigateToMealPlanner: () -> Unit,
    onSignOut: () -> Unit
) {

    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipe App") },
                actions = {
                    TextButton(onClick = onSignOut) {
                        Text("Sign Out")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        onNavigateToMealPlanner()
                    },
                    icon = { Icon(Icons.Default.CalendarMonth, null) },
                    label = { Text("Planner") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Profile") }
                )
            }
        }
    ) { padding ->

        when (selectedTab) {

            0 -> FeedScreen(
                onRecipeClick = { id ->
                    onNavigateToRecipeDetails(id)
                }
            )

            1 -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )

            2 -> Text(
                text = "Profile coming soon",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
