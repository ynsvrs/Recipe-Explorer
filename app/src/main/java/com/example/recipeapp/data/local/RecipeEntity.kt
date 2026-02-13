package com.example.recipeapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


val isFavorite: Boolean = false

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val image: String,
    val page: Int,
    val isFavorite: Boolean = false
)

