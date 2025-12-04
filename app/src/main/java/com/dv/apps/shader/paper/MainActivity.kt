package com.dv.apps.shader.paper

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import com.dv.apps.shader.paper.domain.model.ShaderManifest
import com.dv.apps.shader.paper.ui.theme.ShaderPaperTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLContext

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val egl = EGLContext.getEGL() as EGL10
        val display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
        val version = intArrayOf(0, 0)
        // will always return the minimum version
        // let's improve later
        egl.eglInitialize(display, version)
        egl.eglTerminate(display)

        setContent {
            ShaderPaperTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    LazyColumn(Modifier.padding(innerPadding)) {
                        items(state.items) {
                            ShaderPreview(
                                state.baseUrl,
                                it
                            )
                        }
                        item {
                            ApplyItem()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun ShaderPreview(
    baseUrl: String,
    item: ShaderManifest.ShaderManifestItem,
    modifier: Modifier = Modifier
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
            modifier = Modifier.fillMaxSize().aspectRatio(1f)
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