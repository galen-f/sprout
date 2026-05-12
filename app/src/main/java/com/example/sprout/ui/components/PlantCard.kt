package com.example.sprout.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.sprout.domain.model.Plant
import com.example.sprout.domain.model.WateringStatus
import com.example.sprout.ui.theme.DewGradient
import com.example.sprout.ui.theme.SproutCreamDark

@Composable
fun PlantCard(
    plant: Plant,
    wateringStatus: WateringStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
                if (plant.coverPhotoUri != null) {
                    AsyncImage(
                        model = plant.coverPhotoUri,
                        contentDescription = plant.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize().background(SproutCreamDark),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = plant.name.take(1).uppercase(),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        )
                    }
                }
                Box(
                    modifier = Modifier.fillMaxSize().background(DewGradient),
                )
                StatusPill(
                    status = wateringStatus,
                    modifier = Modifier.align(Alignment.BottomStart).padding(8.dp),
                )
            }
            Text(
                text = plant.name,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            )
            plant.species?.let { species ->
                Text(
                    text = species,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 8.dp),
                )
            }
        }
    }
}
