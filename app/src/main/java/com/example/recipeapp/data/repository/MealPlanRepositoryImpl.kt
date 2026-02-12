package com.example.recipeapp.data.repository
import com.example.recipeapp.domain.model.MealPlan
import com.example.recipeapp.domain.repository.MealPlanRepository
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
import javax.inject.Inject

class MealPlanRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) : MealPlanRepository {

    companion object {
        private const val MEAL_PLANS_PATH = "mealPlans"
    }

    private fun getUserMealPlansRef() =
        database.reference
            .child(MEAL_PLANS_PATH)
            .child(auth.currentUser?.uid.orEmpty())

    override suspend fun addMealPlan(mealPlan: MealPlan): Resource<String> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Resource.Error("Not authenticated")

            val ref = getUserMealPlansRef()
            val key = ref.push().key
                ?: return Resource.Error("Failed to generate key")

            val plan = mealPlan.copy(id = key, userId = userId)
            ref.child(key).setValue(plan).await()
            Resource.Success(key)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add meal plan")
        }
    }

    override suspend fun updateMealPlan(mealPlan: MealPlan): Resource<Unit> {
        return try {
            getUserMealPlansRef().child(mealPlan.id).setValue(mealPlan).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update meal plan")
        }
    }

    override suspend fun deleteMealPlan(mealPlanId: String): Resource<Unit> {
        return try {
            getUserMealPlansRef().child(mealPlanId).removeValue().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete meal plan")
        }
    }

    override fun getMealPlans(): Flow<Resource<List<MealPlan>>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val plans = snapshot.children.mapNotNull {
                    it.getValue(MealPlan::class.java)
                }
                trySend(Resource.Success(plans))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }

        getUserMealPlansRef().addValueEventListener(listener)
        awaitClose { getUserMealPlansRef().removeEventListener(listener) }
    }
}