package com.example.recipeapp

import com.example.recipeapp.domain.model.DayOfWeek
import com.example.recipeapp.domain.model.MealPlan
import com.example.recipeapp.domain.model.MealType
import org.junit.Assert.*
import org.junit.Test

class MealPlanValidationTest {

    @Test
    fun `meal plan with valid data is created correctly`() {
        // Given
        val mealPlan = MealPlan(
            id = "plan1",
            userId = "user1",
            recipeId = 123,
            recipeTitle = "Pasta",
            day = DayOfWeek.MONDAY.displayName,
            mealType = MealType.LUNCH.displayName
        )

        // Then
        assertEquals("plan1", mealPlan.id)
        assertEquals("user1", mealPlan.userId)
        assertEquals(123, mealPlan.recipeId)
        assertEquals("Pasta", mealPlan.recipeTitle)
        assertEquals("Monday", mealPlan.day)
        assertEquals("Lunch", mealPlan.mealType)
    }

    @Test
    fun `DayOfWeek enum has correct display names`() {
        assertEquals("Monday", DayOfWeek.MONDAY.displayName)
        assertEquals("Tuesday", DayOfWeek.TUESDAY.displayName)
        assertEquals("Sunday", DayOfWeek.SUNDAY.displayName)
    }

    @Test
    fun `MealType enum has correct display names`() {
        assertEquals("Breakfast", MealType.BREAKFAST.displayName)
        assertEquals("Lunch", MealType.LUNCH.displayName)
        assertEquals("Dinner", MealType.DINNER.displayName)
        assertEquals("Snack", MealType.SNACK.displayName)
    }

    @Test
    fun `meal plan timestamp is set on creation`() {
        // Given
        val beforeTime = System.currentTimeMillis()
        val mealPlan = MealPlan(
            recipeId = 123,
            recipeTitle = "Test"
        )
        val afterTime = System.currentTimeMillis()

        // Then
        assertTrue(mealPlan.timestamp >= beforeTime)
        assertTrue(mealPlan.timestamp <= afterTime)
    }

    @Test
    fun `DayOfWeek has 7 days`() {
        assertEquals(7, DayOfWeek.entries.size)
    }
}