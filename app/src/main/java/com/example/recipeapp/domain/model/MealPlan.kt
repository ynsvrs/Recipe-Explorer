package com.example.recipeapp.domain.model

data class MealPlan(
    val id: String = "",
    val userId: String = "",
    val recipeId: Int = 0,
    val recipeTitle: String = "",
    val recipeImage: String? = null,
    val day: String = "",
    val mealType: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

enum class DayOfWeek(val displayName: String) {
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday"),
    SUNDAY("Sunday")
}

enum class MealType(val displayName: String) {
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    DINNER("Dinner"),
    SNACK("Snack")
}