package com.dv.apps.shader.paper

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.dv.apps.shader.paper.domain.repository.ShaderRepository
import com.dv.apps.shader.paper.ui.theme.ShaderPaperTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var shaderRepository: ShaderRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShaderPaperTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        Modifier.padding(innerPadding)
                    ) {
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
            }
        }

        lifecycleScope.launch {
            shaderRepository.getManifest().collect {
                println(it)
            }
        }
    }
}