package com.example.sprout.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.sprout.ui.plantlist.PlantListContent
import com.example.sprout.ui.plantlist.PlantListUiState
import com.example.sprout.ui.theme.SproutTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlantListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyStateIsShownWhenNoPlantsExist() {
        composeTestRule.setContent {
            SproutTheme {
                PlantListContent(
                    uiState = PlantListUiState.Empty,
                    onNavigateToPlant = {},
                    onNavigateToAddPlant = {},
                    onNavigateToSettings = {},
                )
            }
        }
        composeTestRule.onNodeWithText("No plants yet").assertIsDisplayed()
    }
}
