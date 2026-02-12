package com.example.recipeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.recipeapp.domain.model.DayOfWeek
import com.example.recipeapp.domain.model.MealType
import com.example.recipeapp.ui.auth.AuthScreen
import com.example.recipeapp.ui.auth.AuthViewModel
import com.example.recipeapp.ui.comments.CommentsScreen
import com.example.recipeapp.ui.details.RecipeDetailsScreen
import com.example.recipeapp.ui.mealplanner.MealPlannerScreen
import com.example.recipeapp.ui.mealplanner.MealPlannerViewModel
import com.example.recipeapp.ui.theme.RecipeAppTheme
import dagger.hilt.android.AndroidEntryPoint
import com.example.recipeapp.ui.feed.FeedScreen

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

enum class Screen {
    AUTH, HOME, RECIPE_DETAILS, MEAL_PLANNER, COMMENTS
}

@Composable
fun RecipeApp(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()

    var currentScreen by remember { mutableStateOf(Screen.AUTH) }
    var selectedRecipeId by remember { mutableStateOf(0) }
    var selectedRecipeName by remember { mutableStateOf("") }

    // Check if user is already authenticated
    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated) {
            currentScreen = Screen.HOME
        }
    }

    when (currentScreen) {
        Screen.AUTH -> {
            AuthScreen(
                viewModel = authViewModel,
                onNavigateToHome = { currentScreen = Screen.HOME }
            )
        }

        Screen.HOME -> {
            HomeScreen(
                onNavigateToRecipeDetails = { recipeId ->
                    selectedRecipeId = recipeId
                    currentScreen = Screen.RECIPE_DETAILS
                },
                onNavigateToMealPlanner = {
                    currentScreen = Screen.MEAL_PLANNER
                },
                onSignOut = {
                    authViewModel.signOut()
                    currentScreen = Screen.AUTH
                }
            )
        }

        Screen.RECIPE_DETAILS -> {
            RecipeDetailsScreen(
                recipeId = selectedRecipeId,
                onNavigateBack = { currentScreen = Screen.HOME },
                onNavigateToComments = { recipeId, recipeName ->
                    selectedRecipeId = recipeId
                    selectedRecipeName = recipeName
                    currentScreen = Screen.COMMENTS
                },
                onAddToMealPlan = { recipeId, recipeTitle, recipeImage, day, mealType ->
                    // TODO: add recipe to MealPlannerViewModel here
                    currentScreen = Screen.MEAL_PLANNER
                }
            )
        }

        Screen.MEAL_PLANNER -> {
            MealPlannerScreen(
                viewModel = hiltViewModel(), // Provide ViewModel
                onNavigateBack = { currentScreen = Screen.HOME } // <-- added lambda
            )
        }

        Screen.COMMENTS -> {
            CommentsScreen(
                recipeId = selectedRecipeId,
                recipeName = selectedRecipeName,
                onNavigateBack = { currentScreen = Screen.RECIPE_DETAILS }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToRecipeDetails: (Int) -> Unit,
    onNavigateToMealPlanner: () -> Unit,
    onSignOut: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
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
                    icon = { Icon(Icons.Default.Home, "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        onNavigateToMealPlanner()
                    },
                    icon = { Icon(Icons.Default.CalendarMonth, "Meal Planner") },
                    label = { Text("Planner") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Person, "Profile") },
                    label = { Text("Profile") }
                )
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> FeedScreen(
                onRecipeClick = { recipeId ->
                    onNavigateToRecipeDetails(recipeId)
                }
            )
            1 -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    MealPlannerScreen(
                        viewModel = hiltViewModel(),
                        onNavigateBack = { selectedTab = 0 } // <-- added lambda
                    )
                }
            }
            2 -> ProfileTab(
                modifier = Modifier.padding(paddingValues),
                userEmail = authState.user?.email ?: "",
                userName = authState.user?.displayName ?: "User"
            )
        }
    }
}

// HomeTab and ProfileTab stay unchanged
@Composable
fun HomeTab(
    modifier: Modifier = Modifier,
    userName: String,
    onNavigateToRecipeDetails: (Int) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Welcome, $userName! 👋",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Test Your Screens",
            style = MaterialTheme.typography.titleLarge
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Partner B Implementation ✅",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("✅ Authentication (Working!)")
                Text("✅ Recipe Details (Mock Data)")
                Text("✅ Meal Planner (Firebase)")
                Text("✅ Realtime Comments (Firebase)")
            }
        }

        HorizontalDivider()

        Text(
            text = "Test Recipes",
            style = MaterialTheme.typography.titleMedium
        )

        // Mock Recipe Cards
        repeat(3) { index ->
            Card(
                onClick = { onNavigateToRecipeDetails(index + 1) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🍝", style = MaterialTheme.typography.headlineLarge)
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Test Recipe ${index + 1}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Click to view details",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "⏱️ 30 min",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "🍽️ 4 servings",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "📝 Note for Partner A",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "These are mock recipes. When you implement the Recipe Feed with real API data, these will be replaced with actual recipes from Spoonacular API.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
fun ProfileTab(
    modifier: Modifier = Modifier,
    userEmail: String,
    userName: String
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Surface(
            modifier = Modifier.size(100.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = userName.firstOrNull()?.uppercase() ?: "U",
                    style = MaterialTheme.typography.displayLarge
                )
            }
        }

        Text(
            text = userName,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = userEmail,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Statistics",
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("Recipes", "0")
                    StatItem("Favorites", "0")
                    StatItem("Plans", "0")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "👤 Partner A TODO",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Implement favorites list and user preferences here.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
