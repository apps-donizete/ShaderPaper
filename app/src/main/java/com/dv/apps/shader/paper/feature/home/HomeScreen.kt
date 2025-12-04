package com.dv.apps.shader.paper.feature.home

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import com.dv.apps.shader.paper.MainService
import com.dv.apps.shader.paper.domain.model.ShaderManifest

@Composable
fun HomeScreen() {
    if (LocalInspectionMode.current) {
        HomeScreen(State())
    } else {
        val viewModel = hiltViewModel<HomeViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()
        HomeScreen(state)
    }
}

@Composable
internal fun HomeScreen(
    state: State
) {
    Surface(Modifier.fillMaxSize()) {
        ShaderManifest(state.shaderManifest)
    }
}

@Composable
fun ShaderManifest(
    item: ShaderManifest
) {
    LazyColumn {
        items(item.items) {
            ShaderPreview(
                item.baseUrl,
                it
            )
        }
        item {
            ApplyItem()
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun ShaderPreview(
    baseUrl: String,
    item: ShaderManifest.ShaderManifestItem
) {
    val context = LocalContext.current
    val player = remember {
        val item = MediaItem.fromUri("$baseUrl/${item.path}/preview.webm")
        ExoPlayer
            .Builder(context)
            .build()
            .apply {
                addMediaItem(item)
                playWhenReady = true
                repeatMode = ExoPlayer.REPEAT_MODE_ONE
                prepare()
            }
    }
    Card(
        Modifier.padding(8.dp)
    ) {
        PlayerSurface(
            player = player,
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
        )
        Text(
            item.title,
            Modifier.padding(8.dp)
        )
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