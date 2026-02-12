package com.example.recipeapp.data.repository
import com.example.recipeapp.domain.model.Comment
import com.example.recipeapp.domain.repository.CommentRepository
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

class CommentRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) : CommentRepository {

    companion object {
        private const val COMMENTS_PATH = "comments"
    }

    private fun getCommentsRef(recipeId: Int) =
        database.reference
            .child(COMMENTS_PATH)
            .child(recipeId.toString())

    override suspend fun addComment(recipeId: Int, text: String): Resource<String> {
        return try {
            val user = auth.currentUser
                ?: return Resource.Error("Not authenticated")

            val ref = getCommentsRef(recipeId)
            val key = ref.push().key
                ?: return Resource.Error("Failed to generate key")

            val comment = Comment(
                id = key,
                recipeId = recipeId,
                userId = user.uid,
                userName = user.email?.substringBefore("@") ?: "Anonymous",
                userEmail = user.email.orEmpty(),
                text = text,
                timestamp = System.currentTimeMillis()
            )

            ref.child(key).setValue(comment).await()
            Resource.Success(key)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add comment")
        }
    }

    override suspend fun updateComment(comment: Comment): Resource<Unit> {
        return try {
            getCommentsRef(comment.recipeId)
                .child(comment.id)
                .setValue(comment)
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update comment")
        }
    }

    override suspend fun deleteComment(recipeId: Int, commentId: String): Resource<Unit> {
        return try {
            getCommentsRef(recipeId)
                .child(commentId)
                .removeValue()
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete comment")
        }
    }

    override fun getComments(recipeId: Int): Flow<Resource<List<Comment>>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val comments = snapshot.children
                    .mapNotNull { it.getValue(Comment::class.java) }
                    .sortedByDescending { it.timestamp }
                trySend(Resource.Success(comments))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }

        getCommentsRef(recipeId).addValueEventListener(listener)
        awaitClose { getCommentsRef(recipeId).removeEventListener(listener) }
    }
}