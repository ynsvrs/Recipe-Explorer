package com.example.recipeapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.recipeapp.ui.auth.AuthScreen
import com.example.recipeapp.ui.comments.CommentsScreen
import com.example.recipeapp.ui.details.RecipeDetailsScreen
import com.example.recipeapp.ui.feed.HomeScreen
import com.example.recipeapp.ui.mealplanner.MealPlannerScreen
import com.example.recipeapp.ui.profile.ProfileScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {

    NavHost(
        navController = navController,
        startDestination = "auth"
    ) {

        composable("auth") {
            AuthScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onNavigateToRecipeDetails = { id ->
                    navController.navigate("details/$id")
                },
                onNavigateToMealPlanner = {
                    navController.navigate("planner")
                },
                onSignOut = {
                    navController.navigate("auth") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("details/{recipeId}") { backStack ->
            val recipeId =
                backStack.arguments?.getString("recipeId")?.toInt() ?: 0

            RecipeDetailsScreen(
                recipeId = recipeId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToComments = { id, name ->
                    navController.navigate("comments/$id/$name")
                },
                onAddToMealPlan = { _, _, _, _, _ ->
                    navController.navigate("planner")
                }
            )

        }

        composable("planner") {
            MealPlannerScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("profile") {
            ProfileScreen()
        }
    }
}
