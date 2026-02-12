package com.example.recipeapp.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.recipeapp.domain.model.DayOfWeek
import com.example.recipeapp.domain.model.MealType
import com.example.recipeapp.ui.mealplanner.AddToMealPlanDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailsScreen(
    recipeId: Int,
    viewModel: RecipeDetailsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToComments: (Int, String) -> Unit,
    onAddToMealPlan: (Int, String, String?, DayOfWeek, MealType) -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showMealPlanDialog by remember { mutableStateOf(false) }

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.recipe?.title ?: "Recipe Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (state.isFavorite)
                                Icons.Default.Favorite
                            else
                                Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (state.isFavorite)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            state.recipe?.let { recipe ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Recipe Image
                    recipe.image?.let { imageUrl ->
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = recipe.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentScale = ContentScale.Crop
                        )
                    } ?: run {
                        // Placeholder when no image
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "🍽️",
                                    style = MaterialTheme.typography.displayLarge
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Title
                        Text(
                            text = recipe.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // Info Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            recipe.readyInMinutes?.let {
                                InfoChip("⏱️ $it min")
                            }
                            recipe.servings?.let {
                                InfoChip("🍽️ $it servings")
                            }
                        }

                        HorizontalDivider()

                        // Summary
                        recipe.summary?.let { summary ->
                            Text(
                                text = "About",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = summary.replace(Regex("<[^>]*>"), ""),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            HorizontalDivider()
                        }

                        // Ingredients
                        recipe.extendedIngredients?.let { ingredients ->
                            Text(
                                text = "Ingredients",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    ingredients.forEach { ingredient ->
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text("•", fontWeight = FontWeight.Bold)
                                            Text(ingredient.original)
                                        }
                                    }
                                }
                            }
                            HorizontalDivider()
                        }

                        // Instructions
                        recipe.instructions?.let { instructions ->
                            Text(
                                text = "Instructions",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = instructions.replace(Regex("<[^>]*>"), ""),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showMealPlanDialog = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Add to Meal Plan")
                            }

                            OutlinedButton(
                                onClick = { onNavigateToComments(recipeId, recipe.title) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Comments")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    if (showMealPlanDialog) {
        AddToMealPlanDialog(
            onDismiss = { showMealPlanDialog = false },
            onConfirm = { day, mealType ->
                state.recipe?.let { recipe ->
                    onAddToMealPlan(
                        recipe.id,
                        recipe.title,
                        recipe.image,
                        day,
                        mealType
                    )
                }
                showMealPlanDialog = false
            }
        )
    }
}

@Composable
private fun InfoChip(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}