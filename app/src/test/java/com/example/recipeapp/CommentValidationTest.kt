package com.example.recipeapp

import com.example.recipeapp.domain.model.Comment
import org.junit.Assert.*
import org.junit.Test

class CommentValidationTest {

    @Test
    fun `comment with valid data is created correctly`() {
        val comment = Comment(
            id = "comment1",
            recipeId = 123,
            userId = "user1",
            userName = "Test User",
            userEmail = "test@example.com",
            text = "Great recipe!",
            timestamp = System.currentTimeMillis()
        )

        assertEquals("comment1", comment.id)
        assertEquals(123, comment.recipeId)
        assertEquals("user1", comment.userId)
        assertEquals("Great recipe!", comment.text)
    }

    @Test
    fun `comment timestamp is set on creation`() {
        val beforeTime = System.currentTimeMillis()
        val comment = Comment(
            recipeId = 123,
            text = "Test"
        )
        val afterTime = System.currentTimeMillis()

        assertTrue(comment.timestamp >= beforeTime)
        assertTrue(comment.timestamp <= afterTime)
    }

    @Test
    fun `comment with empty text is allowed in model`() {
        // Model allows it, validation happens in ViewModel
        val comment = Comment(
            recipeId = 123,
            text = ""
        )

        assertEquals("", comment.text)
    }

    @Test
    fun `comment fields have correct default values`() {
        val comment = Comment()

        assertEquals("", comment.id)
        assertEquals(0, comment.recipeId)
        assertEquals("", comment.userId)
        assertEquals("", comment.userName)
        assertEquals("", comment.userEmail)
        assertEquals("", comment.text)
    }

    @Test
    fun `comment timestamp is positive number`() {
        val comment = Comment(
            recipeId = 123,
            text = "Test"
        )

        assertTrue(comment.timestamp > 0)
    }
}