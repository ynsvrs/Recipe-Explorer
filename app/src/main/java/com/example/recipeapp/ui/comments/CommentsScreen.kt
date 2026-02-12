package com.example.recipeapp.ui.comments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.recipeapp.domain.model.Comment
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsScreen(
    recipeId: Int,
    recipeName: String,
    viewModel: CommentsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var commentText by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(recipeId) {
        viewModel.loadComments(recipeId)
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comments - $recipeName") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Comment Input
            Surface(
                tonalElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Write a comment...") },
                        maxLines = 3,
                        enabled = !state.isAddingComment
                    )

                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                viewModel.addComment(recipeId, commentText)
                                commentText = ""
                            }
                        },
                        enabled = commentText.isNotBlank() && !state.isAddingComment
                    ) {
                        if (state.isAddingComment) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = "Send",
                                tint = if (commentText.isNotBlank())
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.comments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "No comments yet",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Be the first to comment!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = state.comments,
                    key = { it.id }
                ) { comment ->
                    CommentCard(
                        comment = comment,
                        isCurrentUser = comment.userId == currentUserId,
                        onEdit = { newText ->
                            viewModel.updateComment(comment, newText)
                        },
                        onDelete = {
                            viewModel.deleteComment(recipeId, comment.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentCard(
    comment: Comment,
    isCurrentUser: Boolean,
    onEdit: (String) -> Unit,
    onDelete: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = if (isCurrentUser) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = comment.userName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatTimestamp(comment.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isCurrentUser) {
                    Row {
                        IconButton(
                            onClick = { showEditDialog = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    // Edit Dialog
    if (showEditDialog) {
        EditCommentDialog(
            currentText = comment.text,
            onDismiss = { showEditDialog = false },
            onConfirm = { newText ->
                onEdit(newText)
                showEditDialog = false
            }
        )
    }

    // Delete Confirmation
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Comment") },
            text = { Text("Are you sure you want to delete this comment?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun EditCommentDialog(
    currentText: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var editedText by remember { mutableStateOf(currentText) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Comment") },
        text = {
            OutlinedTextField(
                value = editedText,
                onValueChange = { editedText = it },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(editedText) },
                enabled = editedText.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000}m ago"
        diff < 86400_000 -> "${diff / 3600_000}h ago"
        diff < 604800_000 -> "${diff / 86400_000}d ago"
        else -> {
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}