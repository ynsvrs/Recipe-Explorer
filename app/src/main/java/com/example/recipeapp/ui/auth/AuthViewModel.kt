package com.example.recipeapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.domain.model.User
import com.example.recipeapp.domain.repository.AuthRepository
import com.example.recipeapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber  // ← ADD THIS
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        val user = authRepository.getCurrentUser()
        Timber.d("Checking auth status: user=${user?.email}")  // ← ADD THIS
        _authState.value = AuthState(
            user = user,
            isAuthenticated = user != null
        )
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            Timber.d("Attempting sign in: email=$email")  // ← ADD THIS
            _authState.value = AuthState(isLoading = true)

            when (val result = authRepository.signIn(email, password)) {
                is Resource.Success -> {
                    Timber.d("Sign in successful: userId=${result.data?.id}")  // ← ADD THIS
                    _authState.value = AuthState(
                        user = result.data,
                        isAuthenticated = true
                    )
                }
                is Resource.Error -> {
                    Timber.e("Sign in failed: ${result.message}")  // ← ADD THIS
                    _authState.value = AuthState(
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _authState.value = AuthState(isLoading = true)
                }
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            Timber.d("Attempting sign up: email=$email")  // ← ADD THIS
            _authState.value = AuthState(isLoading = true)

            when (val result = authRepository.signUp(email, password)) {
                is Resource.Success -> {
                    Timber.d("Sign up successful: userId=${result.data?.id}")  // ← ADD THIS
                    _authState.value = AuthState(
                        user = result.data,
                        isAuthenticated = true
                    )
                }
                is Resource.Error -> {
                    Timber.e("Sign up failed: ${result.message}")  // ← ADD THIS
                    _authState.value = AuthState(
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _authState.value = AuthState(isLoading = true)
                }
            }
        }
    }

    fun signOut() {
        Timber.d("User signing out")  // ← ADD THIS
        authRepository.signOut()
        _authState.value = AuthState()
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
}