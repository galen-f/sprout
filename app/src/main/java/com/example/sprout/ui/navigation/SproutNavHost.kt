package com.example.sprout.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.sprout.ui.addplant.AddPlantScreen
import com.example.sprout.ui.carehistory.CareHistoryScreen
import com.example.sprout.ui.editplant.EditPlantScreen
import com.example.sprout.ui.plantdetail.PlantDetailScreen
import com.example.sprout.ui.plantlist.PlantListScreen
import com.example.sprout.ui.settings.SettingsScreen

object Routes {
    const val PLANT_LIST = "plant_list"
    const val ADD_PLANT = "add_plant"
    const val PLANT_DETAIL = "plant_detail/{plantId}"
    const val EDIT_PLANT = "edit_plant/{plantId}"
    const val CARE_HISTORY = "care_history/{plantId}"
    const val SETTINGS = "settings"

    fun plantDetail(plantId: Long) = "plant_detail/$plantId"
    fun editPlant(plantId: Long) = "edit_plant/$plantId"
    fun careHistory(plantId: Long) = "care_history/$plantId"
}

@Composable
fun SproutNavHost(
    navController: NavHostController,
    deepLinkPlantId: Long?,
    onDeepLinkConsumed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(deepLinkPlantId) {
        deepLinkPlantId?.let { plantId ->
            navController.navigate(Routes.plantDetail(plantId)) {
                launchSingleTop = true
            }
            onDeepLinkConsumed()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.PLANT_LIST,
        modifier = modifier,
    ) {
        composable(Routes.PLANT_LIST) {
            PlantListScreen(
                onNavigateToPlant = { plantId -> navController.navigate(Routes.plantDetail(plantId)) },
                onNavigateToAddPlant = { navController.navigate(Routes.ADD_PLANT) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
            )
        }
        composable(Routes.ADD_PLANT) {
            AddPlantScreen(
                onNavigateBack = { navController.popBackStack() },
                onPlantSaved = { plantId ->
                    navController.popBackStack()
                    navController.navigate(Routes.plantDetail(plantId))
                },
            )
        }
        composable(
            route = Routes.PLANT_DETAIL,
            arguments = listOf(navArgument("plantId") { type = NavType.LongType }),
        ) {
            PlantDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { plantId -> navController.navigate(Routes.editPlant(plantId)) },
                onNavigateToCareHistory = { plantId -> navController.navigate(Routes.careHistory(plantId)) },
                onPlantDeleted = {
                    navController.popBackStack(Routes.PLANT_LIST, inclusive = false)
                },
            )
        }
        composable(
            route = Routes.EDIT_PLANT,
            arguments = listOf(navArgument("plantId") { type = NavType.LongType }),
        ) {
            EditPlantScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(
            route = Routes.CARE_HISTORY,
            arguments = listOf(navArgument("plantId") { type = NavType.LongType }),
        ) {
            CareHistoryScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlant = { plantId ->
                    navController.popBackStack()
                    navController.navigate(Routes.plantDetail(plantId))
                },
            )
        }
    }
}
