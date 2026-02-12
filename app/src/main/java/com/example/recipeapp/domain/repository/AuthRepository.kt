package com.example.recipeapp.domain.repository
import com.example.recipeapp.domain.model.User
import com.example.recipeapp.util.Resource

interface AuthRepository {
    suspend fun signUp(email: String, password: String): Resource<User>
    suspend fun signIn(email: String, password: String): Resource<User>
    fun signOut()
    fun getCurrentUser(): User?
    fun isUserLoggedIn(): Boolean
}