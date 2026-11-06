package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.AppStrings

enum class NavTab(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME(
        route = "home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "nav_tab_home"
    ),
    SELL(
        route = "sell",
        selectedIcon = Icons.Filled.LocalOffer,
        unselectedIcon = Icons.Outlined.LocalOffer,
        testTag = "nav_tab_sell"
    ),
    STOCK(
        route = "stock",
        selectedIcon = Icons.Filled.BarChart,
        unselectedIcon = Icons.Outlined.BarChart,
        testTag = "nav_tab_stock"
    ),
    HISTORY(
        route = "history",
        selectedIcon = Icons.Filled.History,
        unselectedIcon = Icons.Outlined.History,
        testTag = "nav_tab_history"
    ),
    BUSINESS(
        route = "business",
        selectedIcon = Icons.Filled.Work,
        unselectedIcon = Icons.Outlined.WorkOutline,
        testTag = "nav_tab_business"
    );

    fun label(lang: AppLanguage): String = when (this) {
        HOME -> AppStrings.tabHome(lang)
        SELL -> AppStrings.tabSell(lang)
        STOCK -> AppStrings.tabStock(lang)
        HISTORY -> AppStrings.tabHistory(lang)
        BUSINESS -> AppStrings.tabBusiness(lang)
    }

    fun title(lang: AppLanguage): String = when (this) {
        HOME -> AppStrings.titleHome(lang)
        SELL -> AppStrings.titleSell(lang)
        STOCK -> AppStrings.titleStock(lang)
        HISTORY -> AppStrings.titleHistory(lang)
        BUSINESS -> AppStrings.titleBusiness(lang)
    }
}
