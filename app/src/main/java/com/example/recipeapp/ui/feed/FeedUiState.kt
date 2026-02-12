package com.example.recipeapp.ui.feed

import com.example.recipeapp.domain.model.Recipe

data class FeedUiState(
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEndReached: Boolean = false
)
