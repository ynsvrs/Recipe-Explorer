package com.example.recipeapp.data.repository

import com.example.recipeapp.domain.repository.FavoritesRepository
import com.example.recipeapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) : FavoritesRepository {

    companion object {
        private const val FAVORITES_PATH = "favorites"
    }

    private fun getUserFavoritesRef() =
        database.reference
            .child(FAVORITES_PATH)
            .child(auth.currentUser?.uid.orEmpty())

    override suspend fun addFavorite(recipeId: Int): Resource<Unit> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Resource.Error("Not authenticated")

            Timber.d("Adding recipe $recipeId to favorites")
            getUserFavoritesRef().child(recipeId.toString()).setValue(true).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e("Failed to add favorite: ${e.message}")
            Resource.Error(e.message ?: "Failed to add favorite")
        }
    }

    override suspend fun removeFavorite(recipeId: Int): Resource<Unit> {
        return try {
            Timber.d("Removing recipe $recipeId from favorites")
            getUserFavoritesRef().child(recipeId.toString()).removeValue().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e("Failed to remove favorite: ${e.message}")
            Resource.Error(e.message ?: "Failed to remove favorite")
        }
    }

    override fun getFavorites(): Flow<Resource<List<Int>>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favoriteIds = snapshot.children.mapNotNull {
                    it.key?.toIntOrNull()
                }
                Timber.d("Loaded ${favoriteIds.size} favorites")
                trySend(Resource.Success(favoriteIds))
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("Error loading favorites: ${error.message}")
                trySend(Resource.Error(error.message))
            }
        }

        getUserFavoritesRef().addValueEventListener(listener)
        awaitClose { getUserFavoritesRef().removeEventListener(listener) }
    }

    override suspend fun isFavorite(recipeId: Int): Boolean {
        return try {
            val snapshot = getUserFavoritesRef()
                .child(recipeId.toString())
                .get()
                .await()
            snapshot.exists()
        } catch (e: Exception) {
            Timber.e("Error checking favorite status: ${e.message}")
            false
        }
    }
}