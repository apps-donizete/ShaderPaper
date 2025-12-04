package com.dv.apps.shader.paper.feature.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.dv.apps.shader.paper.domain.model.ShaderManifest
import com.dv.apps.shader.paper.feature.home.HomeScreen
import com.dv.apps.shader.paper.feature.preview.PreviewScreen

@Composable
fun MainNavigation() {
    val backStack = remember { mutableStateListOf<Entry>(Entry.Home) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Entry.Home> {
                HomeScreen {
                    backStack.add(Entry.Preview(it))
                }
            }
            entry<Entry.Preview> {
                PreviewScreen(it.item)
            }
        }
    )
}

sealed interface Entry {
    data object Home : Entry

    data class Preview(
        val item: ShaderManifest.ShaderManifestItem
    ) : Entry
}