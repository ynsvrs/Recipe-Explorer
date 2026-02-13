package com.example.recipeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.recipeapp.domain.model.DayOfWeek
import com.example.recipeapp.domain.model.MealType
import com.example.recipeapp.ui.auth.AuthScreen
import com.example.recipeapp.ui.auth.AuthViewModel
import com.example.recipeapp.ui.comments.CommentsScreen
import com.example.recipeapp.ui.details.RecipeDetailsScreen
import com.example.recipeapp.ui.favorites.FavoritesScreen
import com.example.recipeapp.ui.feed.FeedScreen
import com.example.recipeapp.ui.mealplanner.MealPlannerScreen
import com.example.recipeapp.ui.mealplanner.MealPlannerViewModel
import com.example.recipeapp.ui.profile.ProfileScreen
import com.example.recipeapp.ui.theme.RecipeAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecipeAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RecipeApp()
                }
            }
        }
    }
}

enum class AppScreen {
    AUTH, MAIN, RECIPE_DETAILS, COMMENTS
}

@Composable
fun RecipeApp(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()

    var currentScreen by remember { mutableStateOf(AppScreen.AUTH) }
    var selectedRecipeId by remember { mutableStateOf(0) }
    var selectedRecipeName by remember { mutableStateOf("") }

    // Auto-navigate to main if authenticated
    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated) {
            currentScreen = AppScreen.MAIN
        } else {
            currentScreen = AppScreen.AUTH
        }
    }

    when (currentScreen) {
        AppScreen.AUTH -> {
            AuthScreen(
                viewModel = authViewModel,
                onNavigateToHome = {
                    currentScreen = AppScreen.MAIN
                }
            )
        }

        AppScreen.MAIN -> {
            MainScreen(
                onNavigateToRecipeDetails = { recipeId ->
                    selectedRecipeId = recipeId
                    currentScreen = AppScreen.RECIPE_DETAILS
                },
                onSignOut = {
                    authViewModel.signOut()
                    currentScreen = AppScreen.AUTH
                }
            )
        }

        AppScreen.RECIPE_DETAILS -> {
            val mealPlannerViewModel: MealPlannerViewModel = hiltViewModel() // <-- inject here

            RecipeDetailsScreen(
                recipeId = selectedRecipeId,
                onNavigateBack = {
                    currentScreen = AppScreen.MAIN
                },
                onNavigateToComments = { recipeId, recipeName ->
                    selectedRecipeId = recipeId
                    selectedRecipeName = recipeName
                    currentScreen = AppScreen.COMMENTS
                },
                onAddToMealPlan = { recipeId, recipeTitle, recipeImage, day, mealType ->
                    mealPlannerViewModel.addMealPlan(
                        recipeId,
                        recipeTitle,
                        recipeImage,
                        day,
                        mealType
                    )
                    currentScreen = AppScreen.MAIN
                }
            )
        }


        AppScreen.COMMENTS -> {
            CommentsScreen(
                recipeId = selectedRecipeId,
                recipeName = selectedRecipeName,
                onNavigateBack = {
                    currentScreen = AppScreen.RECIPE_DETAILS
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToRecipeDetails: (Int) -> Unit,
    onSignOut: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.CalendarMonth, "Planner") },
                    label = { Text("Planner") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Favorite, "Favorites") },
                    label = { Text("Favorites") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Person, "Profile") },
                    label = { Text("Profile") }
                )
            }
        }
    )
    { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> FeedScreen(onRecipeClick = onNavigateToRecipeDetails)
                1 -> MealPlannerScreen(onNavigateBack = { selectedTab = 0 })
                2 -> FavoritesScreen(onRecipeClick = onNavigateToRecipeDetails) // <-- new
                3 -> ProfileScreen(onSignOut = onSignOut)
            }
        }

    }
    }