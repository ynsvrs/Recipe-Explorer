package com.example.recipeapp.ui.mealplanner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import com.example.recipeapp.domain.model.MealPlan
import com.example.recipeapp.domain.model.MealType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlannerScreen(
    viewModel: MealPlannerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meal Planner") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
        } else if (state.mealPlans.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "No meal plans yet",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Add recipes from the details screen",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Group by day
                val groupedByDay = state.mealPlans.groupBy { it.day }

                DayOfWeek.entries.forEach { day ->
                    val mealsForDay = groupedByDay[day.displayName] ?: emptyList()

                    if (mealsForDay.isNotEmpty()) {
                        item {
                            Text(
                                text = day.displayName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        items(mealsForDay) { mealPlan ->
                            MealPlanCard(
                                mealPlan = mealPlan,
                                onDelete = { viewModel.deleteMealPlan(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MealPlanCard(
    mealPlan: MealPlan,
    onDelete: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Recipe Image
            mealPlan.recipeImage?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = mealPlan.recipeTitle,
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Recipe Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = mealPlan.mealType,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = mealPlan.recipeTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Delete Button
            IconButton(
                onClick = { onDelete(mealPlan.id) }
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}