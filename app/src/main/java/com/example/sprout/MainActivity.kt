package com.example.sprout

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.sprout.ui.AppViewModel
import com.example.sprout.ui.navigation.SproutNavHost
import com.example.sprout.ui.theme.SproutTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        const val EXTRA_PLANT_ID = "extra_plant_id"
    }

    private var deepLinkPlantId by mutableStateOf<Long?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deepLinkPlantId = intent.getLongExtra(EXTRA_PLANT_ID, -1L).takeIf { it != -1L }
        enableEdgeToEdge()
        setContent {
            val appViewModel: AppViewModel = hiltViewModel()
            val themeMode by appViewModel.themeMode.collectAsStateWithLifecycle()
            SproutTheme(themeMode = themeMode) {
                Surface(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    SproutNavHost(
                        navController = navController,
                        deepLinkPlantId = deepLinkPlantId,
                        onDeepLinkConsumed = { deepLinkPlantId = null },
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val plantId = intent.getLongExtra(EXTRA_PLANT_ID, -1L).takeIf { it != -1L }
        deepLinkPlantId = plantId
    }
}
