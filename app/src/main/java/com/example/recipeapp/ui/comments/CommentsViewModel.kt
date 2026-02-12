package com.example.recipeapp.ui.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.domain.model.Comment
import com.example.recipeapp.domain.repository.CommentRepository
import com.example.recipeapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber  // ← ADD THIS
import javax.inject.Inject

data class CommentsState(
    val comments: List<Comment> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAddingComment: Boolean = false
)

@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val repository: CommentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CommentsState())
    val state: StateFlow<CommentsState> = _state.asStateFlow()

    private val maxRetries = 3 // automatic retry count

    fun loadComments(recipeId: Int) {
        loadCommentsWithRetry(recipeId)
    }

    // Automatic retry implementation
    private fun loadCommentsWithRetry(recipeId: Int) {
        viewModelScope.launch {
            var attempt = 0
            var success = false

            while (attempt < maxRetries && !success) {
                attempt++
                _state.value = _state.value.copy(isLoading = true)

                repository.getComments(recipeId).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _state.value = CommentsState(comments = result.data ?: emptyList())
                            success = true
                        }
                        is Resource.Error -> {
                            _state.value = CommentsState(
                                comments = _state.value.comments, // keep cached comments
                                error = result.message
                            )
                            if (attempt < maxRetries) {
                                val delayTime = 1000L * attempt // 1s, 2s, 3s
                                kotlinx.coroutines.delay(delayTime)
                            }
                        }
                        is Resource.Loading -> {
                            _state.value = _state.value.copy(isLoading = true)
                        }
                    }
                }
            }
        }
    }

    fun addComment(recipeId: Int, text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isAddingComment = true)

            when (val result = repository.addComment(recipeId, text)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(isAddingComment = false)
                    loadComments(recipeId) // refresh after adding
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isAddingComment = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun updateComment(comment: Comment, newText: String) {
        viewModelScope.launch {
            val updatedComment = comment.copy(
                text = newText,
                timestamp = System.currentTimeMillis()
            )
            repository.updateComment(updatedComment)
        }
    }

    fun deleteComment(recipeId: Int, commentId: String) {
        viewModelScope.launch {
            repository.deleteComment(recipeId, commentId)
            loadComments(recipeId) // refresh after deletion
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
