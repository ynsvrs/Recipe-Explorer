package com.example.recipeapp.domain.model

data class Comment(
    val id: String = "",
    val recipeId: Int = 0,
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)