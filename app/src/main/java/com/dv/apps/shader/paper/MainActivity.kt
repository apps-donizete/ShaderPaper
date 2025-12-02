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
import com.dv.apps.shader.paper.ui.theme.ShaderPaperTheme
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET

class MainActivity : ComponentActivity() {
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
            val retrofit: Retrofit = Retrofit.Builder()
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor(HttpLoggingInterceptor())
                        .build()
                )
                .baseUrl("https://raw.githubusercontent.com/apps-donizete/ShaderPaper/refs/heads/main/")
                .build()
            val repository = retrofit.create<Repository>()
            val data = repository.getData()
            val content = data.string()
            println(content)
        }
    }
}

interface Repository {
    @GET("repository/data.txt")
    suspend fun getData(): ResponseBody
}