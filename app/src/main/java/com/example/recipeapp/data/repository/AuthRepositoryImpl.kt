package com.example.recipeapp.data.repository
import com.example.recipeapp.domain.model.User
import com.example.recipeapp.domain.repository.AuthRepository
import com.example.recipeapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override suspend fun signUp(email: String, password: String): Resource<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                Resource.Success(
                    User(
                        id = firebaseUser.uid,
                        email = firebaseUser.email.orEmpty(),
                        displayName = firebaseUser.email?.substringBefore("@").orEmpty()
                    )
                )
            } else {
                Resource.Error("Sign up failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun signIn(email: String, password: String): Resource<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                Resource.Success(
                    User(
                        id = firebaseUser.uid,
                        email = firebaseUser.email.orEmpty(),
                        displayName = firebaseUser.email?.substringBefore("@").orEmpty()
                    )
                )
            } else {
                Resource.Error("Sign in failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    override fun signOut() {
        auth.signOut()
    }

    override fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return User(
            id = firebaseUser.uid,
            email = firebaseUser.email.orEmpty(),
            displayName = firebaseUser.email?.substringBefore("@").orEmpty()
        )
    }

    override fun isUserLoggedIn(): Boolean = auth.currentUser != null
}