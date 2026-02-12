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

    fun loadComments(recipeId: Int) {
        viewModelScope.launch {
            repository.getComments(recipeId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.value = CommentsState(
                            comments = result.data ?: emptyList()
                        )
                    }
                    is Resource.Error -> {
                        _state.value = CommentsState(
                            error = result.message
                        )
                    }
                    is Resource.Loading -> {
                        _state.value = CommentsState(isLoading = true)
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
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}