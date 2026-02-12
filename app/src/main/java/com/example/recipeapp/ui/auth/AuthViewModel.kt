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
        _authState.value = AuthState(
            user = user,
            isAuthenticated = user != null
        )
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState(isLoading = true)

            when (val result = authRepository.signIn(email, password)) {
                is Resource.Success -> {
                    _authState.value = AuthState(
                        user = result.data,
                        isAuthenticated = true
                    )
                }
                is Resource.Error -> {
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
            _authState.value = AuthState(isLoading = true)

            when (val result = authRepository.signUp(email, password)) {
                is Resource.Success -> {
                    _authState.value = AuthState(
                        user = result.data,
                        isAuthenticated = true
                    )
                }
                is Resource.Error -> {
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
        authRepository.signOut()
        _authState.value = AuthState()
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
}