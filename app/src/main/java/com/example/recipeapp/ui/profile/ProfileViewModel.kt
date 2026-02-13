package com.example.recipeapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.domain.repository.AuthRepository
import com.example.recipeapp.domain.repository.FavoritesRepository
import com.example.recipeapp.domain.repository.MealPlanRepository
import com.example.recipeapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class ProfileState(
    val userName: String = "",
    val userEmail: String = "",
    val favoritesCount: Int = 0,
    val plansCount: Int = 0,
    val isLoading: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val favoritesRepository: FavoritesRepository,
    private val mealPlanRepository: MealPlanRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadUserInfo()
        observeFavorites()
        observeMealPlans()
    }

    private fun loadUserInfo() {
        val user = authRepository.getCurrentUser()
        Timber.d("Loading user info: ${user?.email}")
        _state.update {
            it.copy(
                userName = user?.displayName ?: "User",
                userEmail = user?.email ?: ""
            )
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesRepository.getFavorites().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.update {
                            it.copy(favoritesCount = result.data?.size ?: 0)
                        }
                    }
                    is Resource.Error -> {
                        Timber.e("Error loading favorites: ${result.message}")
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    private fun observeMealPlans() {
        viewModelScope.launch {
            mealPlanRepository.getMealPlans().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.update {
                            it.copy(plansCount = result.data?.size ?: 0)
                        }
                    }
                    is Resource.Error -> {
                        Timber.e("Error loading meal plans: ${result.message}")
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun signOut() {
        Timber.d("User signing out from profile")
        authRepository.signOut()
    }
}