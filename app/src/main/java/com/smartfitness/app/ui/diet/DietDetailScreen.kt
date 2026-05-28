package com.smartfitness.app.ui.diet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.smartfitness.app.core.ui.components.SmartFitnessTopBar
import com.smartfitness.app.domain.model.DietItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietDetailScreen(diet: DietItem, navController: NavHostController? = null) {
    Scaffold(
        topBar = {

            SmartFitnessTopBar(
                title = diet.name,
                showBackButton = true,
                onBackClick = {
                    navController?.popBackStack()
                }
            )

        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AsyncImage(
                model = diet.image,
                contentDescription = diet.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )

            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = diet.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = diet.description)

                Spacer(modifier = Modifier.height(16.dp))

                Text("Calories: ${diet.calories} kcal")

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Protein: ${diet.protein}g")
                    Text("Carbs: ${diet.carbs}g")
                    Text("Fat: ${diet.fat}g")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Ingredients", fontWeight = FontWeight.Bold)

                diet.ingredients.forEach {
                    Text("• $it")
                }
            }
        }
    }
}