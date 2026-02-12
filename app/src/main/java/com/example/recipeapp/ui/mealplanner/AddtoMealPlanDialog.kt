package com.example.recipeapp.ui.mealplanner

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.recipeapp.domain.model.DayOfWeek
import com.example.recipeapp.domain.model.MealType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToMealPlanDialog(
    onDismiss: () -> Unit,
    onConfirm: (DayOfWeek, MealType) -> Unit
) {
    var selectedDay by remember { mutableStateOf(DayOfWeek.MONDAY) }
    var selectedMealType by remember { mutableStateOf(MealType.LUNCH) }
    var expandedDay by remember { mutableStateOf(false) }
    var expandedMeal by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to Meal Plan") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Day Selector
                ExposedDropdownMenuBox(
                    expanded = expandedDay,
                    onExpandedChange = { expandedDay = it }
                ) {
                    OutlinedTextField(
                        value = selectedDay.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Day") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDay)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDay,
                        onDismissRequest = { expandedDay = false }
                    ) {
                        DayOfWeek.entries.forEach { day ->
                            DropdownMenuItem(
                                text = { Text(day.displayName) },
                                onClick = {
                                    selectedDay = day
                                    expandedDay = false
                                }
                            )
                        }
                    }
                }

                // Meal Type Selector
                ExposedDropdownMenuBox(
                    expanded = expandedMeal,
                    onExpandedChange = { expandedMeal = it }
                ) {
                    OutlinedTextField(
                        value = selectedMealType.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Meal Type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMeal)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedMeal,
                        onDismissRequest = { expandedMeal = false }
                    ) {
                        MealType.entries.forEach { mealType ->
                            DropdownMenuItem(
                                text = { Text(mealType.displayName) },
                                onClick = {
                                    selectedMealType = mealType
                                    expandedMeal = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedDay, selectedMealType) }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}