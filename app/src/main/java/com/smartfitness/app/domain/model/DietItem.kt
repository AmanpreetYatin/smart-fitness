package com.smartfitness.app.domain.model

data class DietItem(
    val id: Int,
    val name: String,
    val image: String,
    val description: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val ingredients: List<String>
)