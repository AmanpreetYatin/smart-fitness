package com.smartfitness.app.core

import com.smartfitness.app.domain.model.DietItem

object Constants {

    val sampleDietList = listOf(
        DietItem(
            id = 1,
            name = "Grilled Chicken Salad",
            image = "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            description = "Healthy grilled chicken with fresh veggies",
            calories = 350,
            protein = 30,
            carbs = 15,
            fat = 12,
            ingredients = listOf(
                "Chicken Breast",
                "Lettuce",
                "Tomatoes",
                "Olive Oil",
                "Cucumber"
            )
        ),
        DietItem(
            id = 2,
            name = "Oats with Fruits",
            image = "https://images.unsplash.com/photo-1586201375761-83865001e31c",
            description = "High fiber oats with mixed fruits",
            calories = 250,
            protein = 8,
            carbs = 45,
            fat = 5,
            ingredients = listOf(
                "Oats",
                "Milk",
                "Banana",
                "Apple",
                "Honey"
            )
        ),
        DietItem(
            id = 3,
            name = "Paneer Tikka",
            image = "https://images.unsplash.com/photo-1601050690597-df0568f70950",
            description = "Grilled paneer cubes with spices",
            calories = 300,
            protein = 18,
            carbs = 10,
            fat = 20,
            ingredients = listOf(
                "Paneer",
                "Capsicum",
                "Onion",
                "Spices",
                "Yogurt"
            )
        ),
        DietItem(
            id = 4,
            name = "Avocado Toast",
            image = "https://images.unsplash.com/photo-1603046891744-76e6300f8f98",
            description = "Whole grain toast with mashed avocado",
            calories = 280,
            protein = 6,
            carbs = 30,
            fat = 14,
            ingredients = listOf(
                "Bread",
                "Avocado",
                "Salt",
                "Pepper",
                "Lemon Juice"
            )
        ),
        DietItem(
            id = 5,
            name = "Boiled Eggs & Veggies",
            image = "https://images.unsplash.com/photo-1551218808-94e220e084d2",
            description = "Simple protein-rich boiled eggs meal",
            calories = 220,
            protein = 18,
            carbs = 8,
            fat = 12,
            ingredients = listOf(
                "Eggs",
                "Broccoli",
                "Carrot",
                "Salt",
                "Black Pepper"
            )
        ),
        DietItem(
            id = 6,
            name = "Smoothie Bowl",
            image = "https://images.unsplash.com/photo-1490645935967-10de6ba17061",
            description = "Berry smoothie topped with nuts & seeds",
            calories = 270,
            protein = 7,
            carbs = 40,
            fat = 9,
            ingredients = listOf(
                "Berries",
                "Banana",
                "Almonds",
                "Chia Seeds",
                "Yogurt"
            )
        )
    )
}
