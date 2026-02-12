package com.example.recipeapp.domain.repository
import com.example.recipeapp.domain.model.Comment
import com.example.recipeapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface CommentRepository {
    suspend fun addComment(recipeId: Int, text: String): Resource<String>
    suspend fun updateComment(comment: Comment): Resource<Unit>
    suspend fun deleteComment(recipeId: Int, commentId: String): Resource<Unit>
    fun getComments(recipeId: Int): Flow<Resource<List<Comment>>>
}