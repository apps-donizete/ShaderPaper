package com.dv.apps.shader.paper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.dv.apps.shader.paper.feature.home.HomeScreen
import com.dv.apps.shader.paper.feature.home.HomeViewModel
import com.dv.apps.shader.paper.ui.theme.ShaderPaperTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLContext

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<HomeViewModel>()

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
                    HomeScreen(Modifier.padding(innerPadding), viewModel)
                }
            }
        }
    }
}