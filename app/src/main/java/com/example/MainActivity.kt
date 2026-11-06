package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.local.AppSettings
import com.example.ui.components.AppBottomBar
import com.example.ui.components.AppTopBar
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.LanguageController
import com.example.ui.localization.ProvideAppLanguage
import com.example.ui.navigation.NavTab
import com.example.ui.screens.BusinessScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SellScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StockScreen
import com.example.ui.theme.HardwareManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppSettings.init(this)
        enableEdgeToEdge()
        setContent {
            HardwareManagerTheme {
                ProvideAppLanguage {
                    HardwareManagerApp()
                }
            }
        }
    }
}

@Composable
fun HardwareManagerApp() {
    var currentTab by remember { mutableStateOf(NavTab.HOME) }
    var showBakiScreen by remember { mutableStateOf(false) }
    var showSettingsScreen by remember { mutableStateOf(false) }
    val currentLanguage = LanguageController.currentLanguage

    // Android back handler to return from Settings or Baki screen or to Home tab
    BackHandler(enabled = showSettingsScreen || showBakiScreen || currentTab != NavTab.HOME) {
        when {
            showSettingsScreen -> showSettingsScreen = false
            showBakiScreen -> showBakiScreen = false
            else -> currentTab = NavTab.HOME
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                title = when {
                    showSettingsScreen -> {
                        if (currentLanguage == AppLanguage.BANGLA) "সেটিংস ও ডাটা ব্যাকআপ" else "Settings & Backup"
                    }
                    showBakiScreen -> {
                        if (currentLanguage == AppLanguage.BANGLA) "বাকির খাতা (Customer Baki)" else "Customer Baki Ledger"
                    }
                    else -> currentTab.title(currentLanguage)
                },
                currentLanguage = currentLanguage,
                onLanguageChange = { newLang ->
                    LanguageController.setLanguage(newLang)
                },
                canNavigateBack = showSettingsScreen || showBakiScreen || currentTab != NavTab.HOME,
                onNavigateBack = {
                    when {
                        showSettingsScreen -> showSettingsScreen = false
                        showBakiScreen -> showBakiScreen = false
                        else -> currentTab = NavTab.HOME
                    }
                },
                onOpenSettings = {
                    showBakiScreen = false
                    showSettingsScreen = true
                }
            )
        },
        bottomBar = {
            AppBottomBar(
                currentTab = currentTab,
                onTabSelected = { tab ->
                    showSettingsScreen = false
                    showBakiScreen = false
                    currentTab = tab
                },
                currentLanguage = currentLanguage
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                showSettingsScreen -> {
                    SettingsScreen(
                        currentLanguage = currentLanguage,
                        onBack = { showSettingsScreen = false }
                    )
                }
                showBakiScreen -> {
                    com.example.ui.screens.baki.BakiScreen(
                        currentLanguage = currentLanguage,
                        onBack = { showBakiScreen = false }
                    )
                }
                else -> {
                    Crossfade(
                        targetState = currentTab,
                        label = "tab_crossfade"
                    ) { tab ->
                        when (tab) {
                            NavTab.HOME -> HomeScreen(
                                currentLanguage = currentLanguage,
                                onNavigateToSell = { currentTab = NavTab.SELL },
                                onNavigateToStock = { currentTab = NavTab.STOCK },
                                onNavigateToBaki = { showBakiScreen = true },
                                onNavigateToSettings = { showSettingsScreen = true }
                            )
                            NavTab.SELL -> SellScreen(currentLanguage = currentLanguage)
                            NavTab.STOCK -> StockScreen(currentLanguage = currentLanguage)
                            NavTab.HISTORY -> HistoryScreen(currentLanguage = currentLanguage)
                            NavTab.BUSINESS -> BusinessScreen(
                                currentLanguage = currentLanguage,
                                onNavigateToBaki = { showBakiScreen = true }
                            )
                        }
                    }
                }
            }
        }
    }
}
