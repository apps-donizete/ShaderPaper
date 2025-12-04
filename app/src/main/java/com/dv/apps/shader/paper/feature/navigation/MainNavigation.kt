package com.dv.apps.shader.paper.feature.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.dv.apps.shader.paper.feature.home.HomeScreen

@Composable
fun MainNavigation() {
    val backStack = rememberSaveable { mutableStateListOf<Entry>(Entry.Home) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Entry.Home> {
                HomeScreen()
            }
        }
    )
}

sealed interface Entry {
    data object Home : Entry
}