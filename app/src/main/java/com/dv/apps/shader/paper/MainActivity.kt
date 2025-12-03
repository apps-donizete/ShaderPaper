package com.dv.apps.shader.paper

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dv.apps.shader.paper.domain.model.ShaderManifest
import com.dv.apps.shader.paper.ui.theme.ShaderPaperTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShaderPaperTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    LazyColumn(Modifier.padding(innerPadding)) {
                        items(state.items) {
                            ShaderPreview(it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShaderPreview(
    item: ShaderManifest.ShaderManifestItem,
    modifier: Modifier = Modifier
) {
    Card {
        Text(item.title)
    }
}

@Composable
fun ApplyItem(modifier: Modifier = Modifier) {
    Column {
        val context = LocalContext.current
        Button(
            onClick = {
                val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                    .apply {
                        putExtra(
                            WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                            ComponentName(
                                context,
                                MainService::class.java
                            )
                        )
                    }
                context.startActivity(intent)
            }
        ) {
            Text("Click me")
        }
    }
}