package com.example.recipeapp.domain.repository

import com.example.recipeapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun addFavorite(recipeId: Int): Resource<Unit>
    suspend fun removeFavorite(recipeId: Int): Resource<Unit>
    fun getFavorites(): Flow<Resource<List<Int>>>
    suspend fun isFavorite(recipeId: Int): Boolean
}