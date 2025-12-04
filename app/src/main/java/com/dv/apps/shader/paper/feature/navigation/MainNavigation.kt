package com.dv.apps.shader.paper.feature.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.dv.apps.shader.paper.feature.home.HomeScreen

@Composable
fun MainNavigation() {
    val backStack = rememberSaveable { mutableStateListOf(Destination.HOME) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                Destination.HOME -> NavEntry(key) {
                    HomeScreen()
                }
            }
        }
    )
}

enum class Destination {
    HOME
}